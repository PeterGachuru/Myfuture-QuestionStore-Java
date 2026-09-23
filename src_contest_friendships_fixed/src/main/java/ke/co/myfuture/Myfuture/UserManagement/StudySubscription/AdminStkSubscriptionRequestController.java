package ke.co.myfuture.Myfuture.UserManagement.StudySubscription;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/stk-subscription-requests")
public class AdminStkSubscriptionRequestController {

    private final StkSubscriptionRequestRepository repository;

    public AdminStkSubscriptionRequestController(StkSubscriptionRequestRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {

        List<StkSubscriptionRequest> requests = repository.findAllByOrderByCreatedAtDesc(PageRequest.of(0,300));

        model.addAttribute("requests", requests);

        return "admin/stk_subscription_requests";
    }

}