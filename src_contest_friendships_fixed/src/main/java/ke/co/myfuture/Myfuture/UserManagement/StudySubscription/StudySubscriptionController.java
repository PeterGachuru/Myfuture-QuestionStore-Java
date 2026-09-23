package ke.co.myfuture.Myfuture.UserManagement.StudySubscription;

import ke.co.myfuture.Myfuture.MpesaIntegration.MpesaService;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/myfuture/subscriptions")
public class StudySubscriptionController {
    @Autowired
    private StudySubscriptionService studySubscriptionService;
    @Autowired
    IbukaStudentAccountRepository ibukaStudentAccountRepository;
    @Autowired
    StkSubscriptionRequestRepository stkSubscriptionRequestRepository;

    @Autowired
    StudySubscriptionRepository studySubscriptionRepository;

    @Autowired
    MpesaService mpesaService;

    @PostMapping
    public ResponseEntity<StudySubscription> createSubscription(@RequestBody StudySubscription studySubscription) {
        System.out.println("studySubscription: "+studySubscription);
        if (studySubscription.getPaymentProcessor().equalsIgnoreCase("credits")) {
            Optional<IbukaStudentAccount> ibukaStudentAccountOptional = ibukaStudentAccountRepository.findByShareCode(studySubscription.getReferralCode());
            if (ibukaStudentAccountOptional.isEmpty()) {
                System.out.println("no such person with that share code");
                return null;
            }
            Integer currentCredits = ibukaStudentAccountOptional.get().getCreditsBalance();
            currentCredits -= studySubscription.getPayAmount().intValue()*4;
            ibukaStudentAccountOptional.get().setCreditsBalance(currentCredits);
            ibukaStudentAccountRepository.save(ibukaStudentAccountOptional.get());
        }
        StudySubscription subscription = studySubscriptionService.createSubscription(studySubscription);
        return ResponseEntity.ok(subscription);
    }

    @PostMapping("mpesastk")
    public ResponseEntity<StkSubscriptionRequest> stkRequest(@RequestBody StkSubscriptionRequest stkSubscriptionRequest) {
        System.out.println("studySubscription: "+stkSubscriptionRequest);
        stkSubscriptionRequest.setStatus("PENDING");
        StkSubscriptionRequest stkSubscriptionRequestSaved = stkSubscriptionRequestRepository.save(stkSubscriptionRequest);

        mpesaService.initiateStkPush(
                stkSubscriptionRequestSaved.getPhoneNumber(),
                stkSubscriptionRequestSaved.getPayAmount().doubleValue(),
                stkSubscriptionRequestSaved.getInstallId().toString(),
                stkSubscriptionRequestSaved.getId(),
                stkSubscriptionRequestSaved.getSubscriptionType()
        );

        return ResponseEntity.ok(stkSubscriptionRequestSaved);
    }

    @GetMapping("/mpesastk/status/{id}")
    public ResponseEntity<StkSubscriptionRequest> getSubscriptionStkRequestById(@PathVariable Long id) {
        Optional<StkSubscriptionRequest> subscriptionRequest = stkSubscriptionRequestRepository.findById(id);
        return ResponseEntity.ok(subscriptionRequest.get());
    }

    @GetMapping("all")
    public List<StudySubscription> getAllSubscriptions() {
        return studySubscriptionService.getAllSubscriptions();
    }

    @GetMapping("stkRequests")
    public List<StkSubscriptionRequest> getAllStkRequests() {
        return studySubscriptionService.getAllStkRquests();
    }


    @GetMapping("/{id}")
    public ResponseEntity<StudySubscription> getSubscriptionById(@PathVariable Long id) {
        StudySubscription subscription = studySubscriptionService.getSubscriptionById(id);
        return ResponseEntity.ok(subscription);
    }

    @GetMapping("/transaction")
    public ResponseEntity<StudySubscription> getSubscriptionByTransactionCode(@RequestParam("transactionCode") String transactionCode) {
        StudySubscription subscription = studySubscriptionService.getSubscriptionByTransactionCode(transactionCode);
        return ResponseEntity.ok(subscription);
    }

    @GetMapping("/email")
    public ResponseEntity<List<StudySubscription>> getSubscriptionByEmailAddress(@RequestParam("emailAddress") String emailAddress) {
        List<StudySubscription> subscription = studySubscriptionService.getSubscriptionByEmailAddress(emailAddress);
        return ResponseEntity.ok(subscription);
    }
    @GetMapping("/forparent")
    public ResponseEntity<List<StudySubscription>> getSubscriptionListForParent(@RequestParam("emailAddress") String emailAddress) {
        List<StudySubscription> subscriptions = studySubscriptionRepository.findTop10ByEmailAddressOrderByCreatedAtDesc(emailAddress);
        return ResponseEntity.ok(subscriptions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudySubscription> updateSubscription(@PathVariable Long id,
                                                                @RequestParam BigDecimal payAmount,
                                                                @RequestParam int numberOfDays,
                                                                @RequestParam String subscriptionType) {
        StudySubscription updatedSubscription = studySubscriptionService.updateSubscription(id, payAmount, numberOfDays, subscriptionType);
        return ResponseEntity.ok(updatedSubscription);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        studySubscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
