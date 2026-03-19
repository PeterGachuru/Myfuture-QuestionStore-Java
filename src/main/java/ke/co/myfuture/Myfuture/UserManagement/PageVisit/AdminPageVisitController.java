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

    // 1. List visitors with multiple visits
    @GetMapping
    public String listVisitors(Model model) {
        List<VisitorSummary> visitors = repository.findVisitorsWithMultipleVisits();
        model.addAttribute("visitors", visitors);
        return "admin/page_visitors";
    }

    // 2. View visits for one visitor
    @GetMapping("/{visitorId}")
    public String visitorDetails(@PathVariable String visitorId, Model model) {
        List<PageVisit> visits = repository.findByVisitorIdOrderByVisitTimeDesc(visitorId);
        model.addAttribute("visits", visits);
        model.addAttribute("visitorId", visitorId);
        return "admin/page_visits";
    }
}