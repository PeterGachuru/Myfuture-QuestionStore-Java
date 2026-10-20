package ke.co.myfuture.Myfuture.UserManagement.GooglePlaySubscription;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.auth.oauth2.GoogleCredentials;
import ke.co.myfuture.Myfuture.UserManagement.SubscriptionExpiryTrack.SubscriptionExpiryTrack;
import ke.co.myfuture.Myfuture.UserManagement.SubscriptionExpiryTrack.SubscriptionExpiryTrackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.Duration;
import java.util.*;
import java.util.Base64;

@Service
public class GooglePlayRtdnService {
    private static final Logger log = LoggerFactory.getLogger(GooglePlayRtdnService.class);
    private static final String PUBLISHER_SCOPE = "https://www.googleapis.com/auth/androidpublisher";
    private final ObjectMapper mapper;
    private final GooglePlayRtdnEventRepository eventRepository;
    private final GooglePlaySubscriptionRepository subscriptionRepository;
    private final GooglePlayRenewalRepository renewalRepository;
    private final SubscriptionExpiryTrackRepository expiryTrackRepository;

    @Value("${google.play.package-name:ke.co.myfuture}") private String packageName;
    @Value("${google.play.rtdn.push-audience:}") private String pushAudience;
    @Value("${google.play.rtdn.push-service-account:}") private String pushServiceAccount;

    public GooglePlayRtdnService(ObjectMapper mapper, GooglePlayRtdnEventRepository eventRepository,
                                GooglePlaySubscriptionRepository subscriptionRepository,
                                GooglePlayRenewalRepository renewalRepository,
                                SubscriptionExpiryTrackRepository expiryTrackRepository) {
        this.mapper = mapper;
        this.eventRepository = eventRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.renewalRepository = renewalRepository;
        this.expiryTrackRepository = expiryTrackRepository;
    }

    public VerifiedPurchase verifyInitialPurchase(String token) throws Exception {
        if (token == null || token.isBlank()) throw new IllegalArgumentException("purchaseToken is required");
        JsonNode state = fetchVerifiedSubscription(token);
        if (!"SUBSCRIPTION_STATE_ACTIVE".equals(state.path("subscriptionState").asText()))
            throw new IllegalArgumentException("Google Play does not report an active subscription");
        JsonNode selected = null;
        for (JsonNode item : state.path("lineItems")) {
            if (selected == null || item.path("expiryTime").asText("").compareTo(selected.path("expiryTime").asText("")) > 0) selected = item;
        }
        if (selected == null) throw new IllegalArgumentException("Verified Play response has no subscription line item");
        Instant expiry = Instant.parse(selected.path("expiryTime").asText());
        String productId = selected.path("productId").asText("");
        String orderId = selected.path("latestSuccessfulOrderId").asText("");
        if (!expiry.isAfter(Instant.now()) || productId.isBlank() || orderId.isBlank())
            throw new IllegalArgumentException("Google Play purchase has no active paid period");
        Instant start = Instant.parse(state.path("startTime").asText());
        return new VerifiedPurchase(productId, orderId, start, expiry);
    }

    public record VerifiedPurchase(String productId, String orderId, Instant startTime, Instant expiryTime) {
        public int numberOfDays() {
            long seconds = Math.max(1, Duration.between(startTime, expiryTime).getSeconds());
            return (int) Math.min(Integer.MAX_VALUE, (seconds + 86399) / 86400);
        }
    }

    public void acceptPush(String authorization, String body) throws Exception {
        verifyPushIdentity(authorization);
        JsonNode envelope = mapper.readTree(body);
        JsonNode message = envelope.path("message");
        String messageId = message.path("messageId").asText("");
        String encoded = message.path("data").asText("");
        if (messageId.isBlank() || encoded.isBlank()) throw new IllegalArgumentException("Missing Pub/Sub message fields");
        if (eventRepository.existsByMessageId(messageId)) return;

        JsonNode notification;
        try {
            notification = mapper.readTree(Base64.getDecoder().decode(encoded));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid RTDN message data", e);
        }
        String receivedPackage = notification.path("packageName").asText("");
        if (!packageName.equals(receivedPackage)) throw new IllegalArgumentException("Unexpected package name");

        GooglePlayRtdnEvent event = new GooglePlayRtdnEvent();
        event.setMessageId(messageId);
        event.setPackageName(receivedPackage);
        event.setNotificationJson(notification.toString());
        event.setStatus("PENDING");
        JsonNode subscriptionNotification = notification.path("subscriptionNotification");
        if (!subscriptionNotification.isMissingNode()) {
            int type = subscriptionNotification.path("notificationType").asInt(-1);
            event.setEventType(notificationTypeName(type));
            event.setPurchaseToken(subscriptionNotification.path("purchaseToken").asText(null));
            if (event.getPurchaseToken() == null || event.getPurchaseToken().isBlank())
                throw new IllegalArgumentException("Subscription notification is missing purchaseToken");
        } else if (!notification.path("testNotification").isMissingNode()) {
            event.setEventType("TEST");
            event.setStatus("PROCESSED");
            event.setProcessedAt(LocalDateTime.now(ZoneOffset.UTC));
        } else {
            // Other RTDN families are not subscription access events. Durably record and ack them.
            event.setEventType("UNSUPPORTED_NOTIFICATION");
            event.setStatus("PROCESSED");
            event.setProcessedAt(LocalDateTime.now(ZoneOffset.UTC));
        }
        eventRepository.save(event); // durable before acknowledging Pub/Sub
        log.info("Accepted Google Play RTDN messageId={} type={}", messageId, event.getEventType());
    }

    @Scheduled(fixedDelayString = "${google.play.rtdn.retry-delay-ms:60000}")
    public void retryPendingEvents() {
        for (GooglePlayRtdnEvent event : eventRepository.findTop50ByStatusOrderByCreatedAtAsc("PENDING")) {
            try {
                processEvent(event.getId());
            } catch (Exception e) {
                log.warn("Google Play RTDN processing will retry messageId={} error={}", event.getMessageId(), safeMessage(e));
            }
        }
    }

    public void processEvent(Long eventId) throws Exception {
        GooglePlayRtdnEvent event = eventRepository.findById(eventId).orElse(null);
        if (event == null || !"PENDING".equals(event.getStatus())) return;
        event.setAttempts(Optional.ofNullable(event.getAttempts()).orElse(0) + 1);
        try {
            if ("TEST".equals(event.getEventType()) || "UNSUPPORTED_NOTIFICATION".equals(event.getEventType())) {
                markProcessed(event);
                eventRepository.save(event);
                return;
            }
            Optional<GooglePlaySubscription> association = subscriptionRepository.findByPurchaseToken(event.getPurchaseToken());
            if (association.isEmpty()) {
                event.setLastError("Purchase token has not yet been associated with a MyFuture account");
                eventRepository.save(event);
                return; // retained for retry; never create an account association from RTDN
            }
            JsonNode state = fetchVerifiedSubscription(event.getPurchaseToken());
            applyVerifiedState(event, association.get(), state);
            markProcessed(event);
            eventRepository.save(event);
        } catch (Exception e) {
            event.setLastError(safeMessage(e));
            eventRepository.save(event);
            throw e;
        }
    }

    private void applyVerifiedState(GooglePlayRtdnEvent event, GooglePlaySubscription association, JsonNode state) {
        String subscriptionState = state.path("subscriptionState").asText("");
        JsonNode lineItems = state.path("lineItems");
        JsonNode selected = null;
        for (JsonNode item : lineItems) {
            if (selected == null || item.path("expiryTime").asText("").compareTo(selected.path("expiryTime").asText("")) > 0) selected = item;
        }
        if (selected == null) throw new IllegalStateException("Verified Play response has no line items");
        String productId = selected.path("productId").asText(association.getProductId());
        String orderId = selected.path("latestSuccessfulOrderId").asText("");
        String expiryText = selected.path("expiryTime").asText("");
        Instant expiry = Instant.parse(expiryText);
        boolean canAccessThroughExpiry = Set.of("SUBSCRIPTION_STATE_ACTIVE", "SUBSCRIPTION_STATE_CANCELED", "SUBSCRIPTION_STATE_IN_GRACE_PERIOD").contains(subscriptionState);

        if ("SUBSCRIPTION_RENEWED".equals(event.getEventType())
                && "SUBSCRIPTION_STATE_ACTIVE".equals(subscriptionState)
                && expiry.isAfter(Instant.now())
                && !orderId.isBlank()
                && !orderId.equals(association.getOrderId())
                && !renewalRepository.existsByPackageNameAndOrderId(packageName, orderId)) {
            GooglePlayRenewal renewal = new GooglePlayRenewal();
            renewal.setPackageName(packageName);
            renewal.setOrderId(orderId);
            renewal.setPurchaseToken(event.getPurchaseToken());
            renewal.setProductId(productId);
            renewal.setUserId(association.getUserId());
            renewal.setVerifiedAt(LocalDateTime.now(ZoneOffset.UTC));
            renewal.setVerifiedExpiryTime(LocalDateTime.ofInstant(expiry, ZoneOffset.UTC));
            renewalRepository.save(renewal);
            log.info("Recorded verified Play renewal orderId={} state={} expiry={}", orderId, subscriptionState, expiryText);
        }

        // The verified Play expiry is authoritative for this purchase token. Do not add days from an RTDN.
        Long installId = association.getInstallId();
        Optional<SubscriptionExpiryTrack> track = expiryTrackRepository.findByInstallId(installId);
        if (track.isEmpty()) return;
        SubscriptionExpiryTrack current = track.get();
        Date currentExpiry = current.getExpiryDate();
        Date verifiedExpiry = Date.from(expiry);
        if (canAccessThroughExpiry) {
            if (currentExpiry == null || verifiedExpiry.after(currentExpiry)) current.setExpiryDate(verifiedExpiry);
        } else if (("SUBSCRIPTION_REVOKED".equals(event.getEventType()) || "SUBSCRIPTION_STATE_EXPIRED".equals(subscriptionState) || "SUBSCRIPTION_STATE_ON_HOLD".equals(subscriptionState)
                || "SUBSCRIPTION_STATE_PAUSED".equals(subscriptionState) || "SUBSCRIPTION_STATE_PENDING".equals(subscriptionState))
                && (currentExpiry == null || !currentExpiry.after(verifiedExpiry))) {
            current.setExpiryDate(new Date());
        }
        expiryTrackRepository.save(current);
    }

    private JsonNode fetchVerifiedSubscription(String token) throws Exception {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Collections.singleton(PUBLISHER_SCOPE));
        credentials.refreshIfExpired();
        String accessToken = credentials.getAccessToken().getTokenValue();
        String url = "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/"
                + packageName + "/purchases/subscriptionsv2/tokens/" + java.net.URLEncoder.encode(token, StandardCharsets.UTF_8);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(15000);
        connection.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        connection.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
        int status = connection.getResponseCode();
        try (InputStream stream = status >= 200 && status < 300 ? connection.getInputStream() : connection.getErrorStream()) {
            String response = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            if (status < 200 || status >= 300) throw new IllegalStateException("Play Developer API returned HTTP " + status + ": " + response);
            return mapper.readTree(response);
        } finally {
            connection.disconnect();
        }
    }

    private void verifyPushIdentity(String authorization) throws Exception {
        if (authorization == null || !authorization.startsWith("Bearer ") || pushAudience.isBlank() || pushServiceAccount.isBlank())
            throw new SecurityException("Pub/Sub push identity is not configured or missing");
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(pushAudience)).build();
        GoogleIdToken token = verifier.verify(authorization.substring("Bearer ".length()));
        if (token == null || !pushServiceAccount.equalsIgnoreCase(token.getPayload().getEmail())
                || !Boolean.TRUE.equals(token.getPayload().getEmailVerified())) throw new SecurityException("Invalid Pub/Sub push identity");
    }

    private static void markProcessed(GooglePlayRtdnEvent event) {
        event.setStatus("PROCESSED");
        event.setLastError(null);
        event.setProcessedAt(LocalDateTime.now(ZoneOffset.UTC));
    }

    private static String notificationTypeName(int type) {
        return switch (type) {
            case 1 -> "SUBSCRIPTION_RECOVERED"; case 2 -> "SUBSCRIPTION_RENEWED";
            case 3 -> "SUBSCRIPTION_CANCELED"; case 4 -> "SUBSCRIPTION_PURCHASED";
            case 5 -> "SUBSCRIPTION_ON_HOLD"; case 6 -> "SUBSCRIPTION_IN_GRACE_PERIOD";
            case 7 -> "SUBSCRIPTION_RESTARTED"; case 9 -> "SUBSCRIPTION_DEFERRED";
            case 10 -> "SUBSCRIPTION_PAUSED"; case 12 -> "SUBSCRIPTION_REVOKED";
            case 13 -> "SUBSCRIPTION_EXPIRED"; default -> "SUBSCRIPTION_TYPE_" + type;
        };
    }

    private static String safeMessage(Exception e) {
        String message = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        return message.length() > 950 ? message.substring(0, 950) : message;
    }
}
