package ke.co.myfuture.Myfuture.UserManagement.StudySubscription;

import ke.co.myfuture.Myfuture.UserManagement.SubscriptionExpiryTrack.SubscriptionExpiryTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StudySubscriptionService {

    @Autowired
    private StudySubscriptionRepository studySubscriptionRepository;

    @Autowired
    private StkSubscriptionRequestRepository stkSubscriptionRequestRepository;

    @Autowired
    private SubscriptionExpiryTrackService subscriptionExpiryTrackService;

    public StudySubscription createSubscription(StudySubscription subscription) {
        if (subscription.getId() != null && subscription.getId() > 0)
            return null;
        StudySubscription savedStudySubscription =  studySubscriptionRepository.save(subscription);

        subscriptionExpiryTrackService.receiveSubscription(savedStudySubscription);

        return savedStudySubscription;
    }

    public List<StudySubscription> getAllSubscriptions() {
        return studySubscriptionRepository.findLatest300();
    }



    public List<StkSubscriptionRequest> getAllStkRquests() {
        return stkSubscriptionRequestRepository.findLatest300();
    }

    public StudySubscription getSubscriptionById(Long id) {
        return studySubscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }

    public StudySubscription getSubscriptionByTransactionCode(String transactionCode) {
        return studySubscriptionRepository.findByTransactionCode(transactionCode);
    }

    public List<StudySubscription> getSubscriptionByEmailAddress(String emailAddress) {
        return studySubscriptionRepository.findTop10ByEmailAddressOrderByCreatedAtDesc(emailAddress);
    }

    public StudySubscription updateSubscription(Long id, BigDecimal payAmount, int numberOfDays, String subscriptionType) {
        StudySubscription subscription = getSubscriptionById(id);
        subscription.setPayAmount(payAmount);
        subscription.setNumberOfDays(numberOfDays);
        subscription.setSubscriptionType(subscriptionType);
        return studySubscriptionRepository.save(subscription);
    }

    public void deleteSubscription(Long id) {
        studySubscriptionRepository.deleteById(id);
    }

    public void stkPaymentSuccessful(Long transactionReferenceId, String mpesaReceiptNumber) {
        Optional<StkSubscriptionRequest> stkSubscriptionRequest = stkSubscriptionRequestRepository.findById(transactionReferenceId);
        if (stkSubscriptionRequest.isPresent()) {
            stkSubscriptionRequest.get().setTransactionCode(mpesaReceiptNumber);
            stkSubscriptionRequest.get().setCallbackAt(new Date());
            stkSubscriptionRequest.get().setStatus("SUCCESS");
            stkSubscriptionRequestRepository.save(stkSubscriptionRequest.get());

            StudySubscription studySubscription = createSubscriptionFromStk(stkSubscriptionRequest.get());
            StudySubscription savedStudySubscription = studySubscriptionRepository.save(studySubscription);
            stkSubscriptionRequest.get().setSubscription(savedStudySubscription);

            stkSubscriptionRequestRepository.save(stkSubscriptionRequest.get());
            subscriptionExpiryTrackService.receiveSubscription(savedStudySubscription);
        }else {
            System.out.println("No StkSubscriptionRequest with id "+transactionReferenceId);
        }
    }

    public StudySubscription createSubscriptionFromStk(StkSubscriptionRequest req) {
        StudySubscription sub = new StudySubscription();
        sub.setPayAmount(req.getPayAmount());
        sub.setNumberOfDays(req.getNumberOfDays());
        sub.setAppVersion(req.getAppVersion());
        sub.setInstallId(req.getInstallId());
        sub.setTransactionCode(req.getTransactionCode());
        sub.setStartDate(req.getStartDate());
        sub.setEndDate(req.getEndDate());
        sub.setEmailAddress(req.getEmailAddress());
        sub.setPhoneNumber(req.getPhoneNumber());
        sub.setCalculated(false);

        sub.setSubscriptionType(req.getSubscriptionType());
        sub.setPaymentProcessor("MPESA");
        return sub;
    }

    public void stkPaymentFailed(Long transactionReferenceId) {
        Optional<StkSubscriptionRequest> stkSubscriptionRequest = stkSubscriptionRequestRepository.findById(transactionReferenceId);
        if (stkSubscriptionRequest.isPresent()) {
            stkSubscriptionRequest.get().setCallbackAt(new Date());
            stkSubscriptionRequest.get().setStatus("FAILED");
            stkSubscriptionRequestRepository.save(stkSubscriptionRequest.get());
        }
    }
}