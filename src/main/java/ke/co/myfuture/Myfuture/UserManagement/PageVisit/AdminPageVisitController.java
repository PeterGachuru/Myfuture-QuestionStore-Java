package ke.co.myfuture.Myfuture.UserManagement.PageVisit;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import java.util.List;

@Controller
@RequestMapping("/admin/page-visits")
public class AdminPageVisitController {

    private final PageVisitRepository repository;

    public AdminPageVisitController(PageVisitRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {
        List<PageVisit> visits = repository.findAllByOrderByVisitTimeDesc(PageRequest.of(0, 300));
        model.addAttribute("visits", visits);
        return "admin/page_visits";
    }
}
