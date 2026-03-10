package ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.AllTimeScore;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/all-time-scores")
public class AdminAllTimeScoreController {

    private final AllTimeScoreRepository repository;

    public AdminAllTimeScoreController(AllTimeScoreRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {

        List<AllTimeScore> scores = repository.findAllByOrderByScoreDesc(PageRequest.of(0, 300));

        model.addAttribute("scores", scores);

        return "admin/all_time_scores";
    }

}