package ke.co.myfuture.Myfuture.UserManagement.GooglePlaySubscription;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/subscriptions/googleplay/rtdn")
public class GooglePlayRtdnController {
    private final GooglePlayRtdnService service;

    public GooglePlayRtdnController(GooglePlayRtdnService service) {
        this.service = service;
    }

    @PostMapping("/push")
    public ResponseEntity<Void> receivePush(@RequestHeader(value = "Authorization", required = false) String authorization,
                                            @RequestBody String body,
                                            HttpServletRequest request) {
        try {
            service.acceptPush(authorization, body);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // Non-2xx tells Pub/Sub to retry when durable acceptance fails.
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}
