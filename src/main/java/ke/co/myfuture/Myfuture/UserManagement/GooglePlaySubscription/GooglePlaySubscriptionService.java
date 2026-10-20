package ke.co.myfuture.Myfuture.UserManagement.GooglePlaySubscription;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.UserManagement.StudySubscription.StudySubscription;
import ke.co.myfuture.Myfuture.UserManagement.StudySubscription.StudySubscriptionRepository;
import ke.co.myfuture.Myfuture.UserManagement.SubscriptionExpiryTrack.SubscriptionExpiryTrackService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
public class GooglePlaySubscriptionService {

    private final GooglePlaySubscriptionRepository repository;
    private final StudySubscriptionRepository studySubscriptionRepository;
    private final SubscriptionExpiryTrackService subscriptionExpiryTrackService;
    private final UserRepository userRepository;
    private final GooglePlayRtdnService googlePlayRtdnService;

    public GooglePlaySubscriptionService(
            GooglePlaySubscriptionRepository repository,
            StudySubscriptionRepository studySubscriptionRepository,
            SubscriptionExpiryTrackService subscriptionExpiryTrackService,
            UserRepository userRepository,
            GooglePlayRtdnService googlePlayRtdnService) {

        this.repository = repository;
        this.studySubscriptionRepository = studySubscriptionRepository;
        this.subscriptionExpiryTrackService = subscriptionExpiryTrackService;
        this.userRepository = userRepository;
        this.googlePlayRtdnService = googlePlayRtdnService;
    }

    public GooglePlaySubscription saveSubscription(
            GooglePlaySubscription subscription) {

        System.out.println("Google Play subscription received:");
        System.out.println(subscription);

        // ---------------------------------------------------------
        // 1. Validate installId
        // ---------------------------------------------------------

        if (subscription.getInstallId() == null) {
            throw new IllegalArgumentException(
                    "installId is required for Google Play subscription");
        }

        // Client price, order ID, duration and timestamps are hints only. Verify the
        // token with Google before granting access or recording a paid transaction.
        GooglePlayRtdnService.VerifiedPurchase verified;
        try {
            verified = googlePlayRtdnService.verifyInitialPurchase(subscription.getPurchaseToken());
        } catch (Exception e) {
            throw new IllegalArgumentException("Google Play could not verify this active purchase", e);
        }
        if (subscription.getProductId() != null && !subscription.getProductId().equals(verified.productId())) {
            throw new IllegalArgumentException("Purchase product does not match Google Play verification");
        }
        subscription.setProductId(verified.productId());
        subscription.setOrderId(verified.orderId());
        subscription.setPurchaseTime(verified.startTime().toEpochMilli());
        subscription.setNumberOfDays(verified.numberOfDays());
        // SubscriptionsV2 does not return a verified price. Do not use a client
        // amount/currency as financial evidence.
        subscription.setAmount(0.0);
        subscription.setCurrency(null);
        subscription.setOriginalJson(null);

        // ---------------------------------------------------------
        // 2. Prevent duplicate Google Play purchase
        // ---------------------------------------------------------

        if (repository.existsByPurchaseToken(
                subscription.getPurchaseToken())) {

            throw new IllegalArgumentException(
                    "Duplicate subscription detected");
        }

        // ---------------------------------------------------------
        // 3. Find the MyFuture user
        // ---------------------------------------------------------

        Optional<User> optionalUser =
                userRepository.findById(subscription.getUserId());

        if (optionalUser.isEmpty()) {

            throw new IllegalArgumentException(
                    "No MyFuture user found with ID: "
                            + subscription.getUserId());
        }

        User user = optionalUser.get();

        System.out.println(
                "Google Play payment belongs to user: "
                        + user.getId()
                        + " / "
                        + user.getEmail());

        // ---------------------------------------------------------
        // 4. Save Google Play payment record
        // ---------------------------------------------------------

        GooglePlaySubscription savedGooglePlaySubscription =
                repository.save(subscription);

        System.out.println(
                "Google Play subscription saved with ID: "
                        + savedGooglePlaySubscription.getId());

        // ---------------------------------------------------------
        // 5. Create StudySubscription
        // ---------------------------------------------------------

        StudySubscription studySubscription =
                new StudySubscription();

        studySubscription.setPayAmount(BigDecimal.ZERO);

        studySubscription.setNumberOfDays(
                subscription.getNumberOfDays());

        studySubscription.setAppVersion(0);

        /*
         * IMPORTANT:
         *
         * Carry the installId supplied by the Android client
         * into StudySubscription.
         */
        studySubscription.setInstallId(
                subscription.getInstallId());

        /*
         * Keep the user's email so that
         * SubscriptionExpiryTrackService can identify the user.
         */
        studySubscription.setEmailAddress(
                user.getEmail());

        /*
         * Google Play order ID becomes the transaction code.
         */
        studySubscription.setTransactionCode(
                subscription.getOrderId());

        /*
         * Google Play product ID, e.g. cbc_weekly.
         */
        studySubscription.setSubscriptionType(
                subscription.getProductId());

        /*
         * Identify the payment source.
         */
        studySubscription.setPaymentProcessor(
                "GOOGLE_PLAY");

        /*
         * These are initially populated because the fields are
         * required by the StudySubscription entity.
         *
         * receiveSubscription() will calculate/update the
         * actual subscription period.
         */
        studySubscription.setStartDate(Date.from(verified.startTime()));
        studySubscription.setEndDate(Date.from(verified.expiryTime()));

        studySubscription.setCalculated(false);

        // ---------------------------------------------------------
        // 6. Save StudySubscription
        // ---------------------------------------------------------

        StudySubscription savedStudySubscription =
                studySubscriptionRepository.save(
                        studySubscription);

        System.out.println(
                "StudySubscription saved with ID: "
                        + savedStudySubscription.getId());

        // ---------------------------------------------------------
        // 7. Apply subscription to user's expiry track
        // ---------------------------------------------------------

        subscriptionExpiryTrackService.receiveVerifiedGooglePlaySubscription(
                savedStudySubscription, Date.from(verified.expiryTime()));

        System.out.println(
                "Google Play subscription successfully applied "
                        + "to user's SubscriptionExpiryTrack");

        return savedGooglePlaySubscription;
    }
}
