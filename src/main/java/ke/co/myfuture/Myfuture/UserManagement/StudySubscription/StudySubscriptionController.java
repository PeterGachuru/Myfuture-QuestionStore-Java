package ke.co.myfuture.Myfuture.UserManagement.StudySubscription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/subscriptions")
public class StudySubscriptionController {

    @Autowired
    private StudySubscriptionService studySubscriptionService;

    @PostMapping
    public ResponseEntity<StudySubscription> createSubscription(@RequestBody StudySubscription studySubscription) {
        System.out.println("studySubscription: "+studySubscription);
        StudySubscription subscription = studySubscriptionService.createSubscription(studySubscription);
        return ResponseEntity.ok(subscription);
    }

    @GetMapping
    public List<StudySubscription> getAllSubscriptions() {
        return studySubscriptionService.getAllSubscriptions();
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
    public ResponseEntity<StudySubscription> getSubscriptionByEmailAddress(@RequestParam("emailAddress") String emailAddress) {
        StudySubscription subscription = studySubscriptionService.getSubscriptionByEmailAddress(emailAddress);
        return ResponseEntity.ok(subscription);
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
