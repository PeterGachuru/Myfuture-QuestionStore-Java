package ke.co.myfuture.Myfuture.UserManagement.GooglePlaySubscription;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions/googleplay")
public class GooglePlaySubscriptionController {

    private final GooglePlaySubscriptionService subscriptionService;

    public GooglePlaySubscriptionController(GooglePlaySubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/record")
    public ResponseEntity<?> recordSubscription(@RequestBody GooglePlaySubscription subscription) {
        try {
            GooglePlaySubscription saved = subscriptionService.saveSubscription(subscription);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}