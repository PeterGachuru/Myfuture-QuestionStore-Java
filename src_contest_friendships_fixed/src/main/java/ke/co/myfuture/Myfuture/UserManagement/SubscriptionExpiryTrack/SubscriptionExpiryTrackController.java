package ke.co.myfuture.Myfuture.UserManagement.SubscriptionExpiryTrack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/myfuture/subscription-expiry")
public class SubscriptionExpiryTrackController {
    @Autowired
    private SubscriptionExpiryTrackService service;

    @GetMapping
    public ResponseEntity<SubscriptionExpiryTrack> getSubscriptionExpiry(
            @RequestParam(required = false) Long installId,
            @RequestParam(required = false) String parentUsername) {

        if (installId == null && parentUsername == null) {
            return ResponseEntity.badRequest()
                    .body(null);
        }

        SubscriptionExpiryTrack track =
                service.getByInstallIdOrUsername(installId, parentUsername);

        return ResponseEntity.ok(track);
    }
}