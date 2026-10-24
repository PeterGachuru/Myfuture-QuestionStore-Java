package ke.co.myfuture.Myfuture.Commonauth.Fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import ke.co.myfuture.Myfuture.Commonauth.Install.Install2Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/** Sends standalone email notifications to Android installations registered to recipients. */
@Service
public class FcmNotificationService {
    private static final Logger log = LoggerFactory.getLogger(FcmNotificationService.class);
    private static final String FIREBASE_APP_NAME = "myfuture-messaging";
    private static final int MAX_TOKENS_PER_REQUEST = 500;

    private final Install2Repository installRepository;
    private final String firebaseProjectId;
    private volatile FirebaseMessaging messaging;
    private volatile boolean initializationAttempted;

    public FcmNotificationService(
            Install2Repository installRepository,
            @Value("${myfuture.firebase.project-id:}") String firebaseProjectId
    ) {
        this.installRepository = installRepository;
        this.firebaseProjectId = firebaseProjectId;
    }

    /** Sensitive account messages receive a generic alert; other messages include their email content. */
    @Async
    public void notifyEmailRecipients(Collection<String> recipientEmails, String subject, String body) {
        Set<String> emails = normalizeEmails(recipientEmails);
        if (emails.isEmpty()) return;

        FirebaseMessaging firebaseMessaging = getMessaging();
        if (firebaseMessaging == null) return;

        try {
            List<String> tokens = installRepository.findAndroidFcmTokensByAccountEmails(emails)
                    .stream()
                    .filter(token -> token != null && !token.isBlank())
                    .distinct()
                    .toList();
            if (tokens.isEmpty()) {
                log.debug("No registered Android FCM installations matched {} email recipient(s)", emails.size());
                return;
            }

            for (int start = 0; start < tokens.size(); start += MAX_TOKENS_PER_REQUEST) {
                List<String> batch = tokens.subList(start, Math.min(start + MAX_TOKENS_PER_REQUEST, tokens.size()));
                sendBatch(firebaseMessaging, batch, subject, body);
            }
        } catch (Exception exception) {
            // Keep notification errors isolated from the caller's email-delivery flow.
            log.warn("Could not send email companion notification to registered Android installations: {}",
                    exception.getMessage());
        }
    }

    private void sendBatch(FirebaseMessaging firebaseMessaging, List<String> tokens, String subject, String body) {
        boolean sensitive = isSensitive(subject, body);
        String title = sensitive ? "MyFuture account security" : cleanTitle(subject);
        String content = sensitive
                ? "An account security email is ready. Open your email app to view it securely."
                : plainText(body);
        List<String> parts = splitUtf8(content, 2800);
        for (int part = 0; part < parts.size(); part++) {
            String partTitle = parts.size() == 1 ? title : title + " (" + (part + 1) + "/" + parts.size() + ")";
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder().setTitle(partTitle).setBody(parts.get(part)).build())
                    .putData("screen", "home")
                    .putData("notification_type", "email_received")
                    .putData("message_part", Integer.toString(part + 1))
                    .putData("message_parts", Integer.toString(parts.size()))
                    .addAllTokens(tokens)
                    .build();
            try {
            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);
            log.info("Email companion FCM delivery completed: sent={}, failed={}",
                    response.getSuccessCount(), response.getFailureCount());
            for (int index = 0; index < response.getResponses().size(); index++) {
                var sendResponse = response.getResponses().get(index);
                if (sendResponse.isSuccessful()) continue;
                FirebaseMessagingException exception = sendResponse.getException();
                if (exception != null && exception.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    installRepository.clearFcmToken(tokens.get(index));
                    log.debug("Removed an expired FCM token from its install record");
                } else if (exception != null) {
                    log.debug("FCM delivery to one installation failed: {}", exception.getMessagingErrorCode());
                }
            }
            } catch (FirebaseMessagingException exception) {
                log.warn("FCM batch send failed: {}", exception.getMessagingErrorCode());
            }
        }
    }

    private static String cleanTitle(String subject) {
        if (subject == null || subject.isBlank()) return "MyFuture";
        String title = subject.replaceAll("[\\r\\n]", " ").trim();
        return title.length() > 120 ? title.substring(0, 120) : title;
    }

    private static String plainText(String body) {
        if (body == null || body.isBlank()) return "(This email has no message text.)";
        String text = Jsoup.parse(body).text().trim();
        return text.isBlank() ? "(This email has no message text.)" : text;
    }

    private static boolean isSensitive(String subject, String body) {
        String text = ((subject == null ? "" : subject) + " " + (body == null ? "" : Jsoup.parse(body).text()))
                .toLowerCase(Locale.ROOT);
        return text.matches("(?s).*\\b(password|passcode|otp|one[- ]time code|reset code|reset token|verification code|credentials?)\\b.*");
    }

    // The FCM payload limit is 4 KB. Send larger emails as ordered, numbered notification parts.
    private static List<String> splitUtf8(String value, int maxBytes) {
        List<String> parts = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        int bytes = 0;
        for (int offset = 0; offset < value.length();) {
            int codePoint = value.codePointAt(offset);
            String chars = new String(Character.toChars(codePoint));
            int size = chars.getBytes(StandardCharsets.UTF_8).length;
            if (bytes + size > maxBytes && current.length() > 0) {
                parts.add(current.toString());
                current.setLength(0);
                bytes = 0;
            }
            current.append(chars);
            bytes += size;
            offset += Character.charCount(codePoint);
        }
        if (current.length() > 0) parts.add(current.toString());
        return parts.isEmpty() ? List.of("") : parts;
    }

    private FirebaseMessaging getMessaging() {
        FirebaseMessaging current = messaging;
        if (current != null) return current;
        synchronized (this) {
            if (messaging != null) return messaging;
            if (initializationAttempted) return null;
            initializationAttempted = true;
            try {
                FirebaseApp app = FirebaseApp.getApps().stream()
                        .filter(candidate -> FIREBASE_APP_NAME.equals(candidate.getName()))
                        .findFirst()
                        .orElseGet(() -> {
                            try {
                                FirebaseOptions.Builder options = FirebaseOptions.builder()
                                        .setCredentials(GoogleCredentials.getApplicationDefault());
                                if (firebaseProjectId != null && !firebaseProjectId.isBlank()) {
                                    options.setProjectId(firebaseProjectId.trim());
                                }
                                return FirebaseApp.initializeApp(options.build(), FIREBASE_APP_NAME);
                            } catch (Exception exception) {
                                throw new FcmInitializationException(exception);
                            }
                        });
                messaging = FirebaseMessaging.getInstance(app);
                log.info("Firebase Cloud Messaging initialized for project {}",
                        firebaseProjectId == null || firebaseProjectId.isBlank() ? "from ADC" : firebaseProjectId);
                return messaging;
            } catch (Exception exception) {
                Throwable cause = exception instanceof FcmInitializationException && exception.getCause() != null
                        ? exception.getCause() : exception;
                log.warn("FCM companion notifications are unavailable; configure Google Application Default Credentials. {}",
                        cause.getMessage());
                return null;
            }
        }
    }

    private static Set<String> normalizeEmails(Collection<String> recipientEmails) {
        if (recipientEmails == null) return Set.of();
        return recipientEmails.stream()
                .filter(email -> email != null && !email.isBlank())
                .map(email -> email.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static final class FcmInitializationException extends RuntimeException {
        private FcmInitializationException(Throwable cause) { super(cause); }
    }
}
