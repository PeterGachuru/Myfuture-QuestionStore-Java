package ke.co.myfuture.Myfuture.UserManagement.SubscriptionExpiryTrack;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import java.util.List;

@Controller
@RequestMapping("/admin/subscription-expiry-tracks")
public class AdminSubscriptionExpiryTrackController {

    private final SubscriptionExpiryTrackRepository repository;

    public AdminSubscriptionExpiryTrackController(SubscriptionExpiryTrackRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {

        List<SubscriptionExpiryTrack> tracks = repository.findAllByOrderByExpiryDateDesc(PageRequest.of(0, 300));

        model.addAttribute("tracks", tracks);

        return "admin/subscription_expiry_tracks";
    }
}
