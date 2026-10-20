package ke.co.myfuture.Myfuture.UserManagement.GooglePlaySubscription;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions/googleplay")
public class GooglePlaySubscriptionController {

    private final GooglePlaySubscriptionService subscriptionService;
    private final UserRepository userRepository;

    public GooglePlaySubscriptionController(GooglePlaySubscriptionService subscriptionService, UserRepository userRepository) {
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
    }

    @PostMapping("/record")
    public ResponseEntity<?> recordSubscription(
            @RequestBody GooglePlaySubscription subscription,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean authenticated = authentication != null
                    && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal());

            if (authenticated) {
                var user = userRepository.findByEmail(authentication.getName());
                if (user.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authenticated account was not found");
                // A valid token is authoritative; ignore a conflicting client-supplied userId.
                subscription.setUserId(user.get().getId());
            } else if (authorization != null && !authorization.isBlank()) {
                // Do not downgrade a malformed or expired token to legacy userId authentication.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bearer token is invalid or expired");
            } else if (subscription.getUserId() == null
                    || !userRepository.existsById(subscription.getUserId())) {
                return ResponseEntity.badRequest().body("A valid userId is required when no bearer token is supplied");
            }

            // Legacy Android clients without JWTs continue using their existing userId field.
            GooglePlaySubscription saved = subscriptionService.saveSubscription(subscription);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
