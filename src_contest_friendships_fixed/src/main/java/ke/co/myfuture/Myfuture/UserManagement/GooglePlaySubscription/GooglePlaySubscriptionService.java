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

    public GooglePlaySubscriptionService(
            GooglePlaySubscriptionRepository repository,
            StudySubscriptionRepository studySubscriptionRepository,
            SubscriptionExpiryTrackService subscriptionExpiryTrackService,
            UserRepository userRepository) {

        this.repository = repository;
        this.studySubscriptionRepository = studySubscriptionRepository;
        this.subscriptionExpiryTrackService = subscriptionExpiryTrackService;
        this.userRepository = userRepository;
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

        studySubscription.setPayAmount(
                BigDecimal.valueOf(subscription.getAmount()));

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
        studySubscription.setStartDate(new Date());
        studySubscription.setEndDate(new Date());

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

        subscriptionExpiryTrackService.receiveSubscription(
                savedStudySubscription);

        System.out.println(
                "Google Play subscription successfully applied "
                        + "to user's SubscriptionExpiryTrack");

        return savedGooglePlaySubscription;
    }
}