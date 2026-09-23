package ke.co.myfuture.Myfuture.UserManagement.StudySubscription;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/study-subscriptions")
public class AdminStudySubscriptionController {

    private final StudySubscriptionRepository repository;

    public AdminStudySubscriptionController(StudySubscriptionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {

        List<StudySubscription> subscriptions = repository.findAllByOrderByCreatedAtDesc(PageRequest.of(0,300));

        model.addAttribute("subscriptions", subscriptions);

        return "admin/study_subscriptions";
    }

}