package ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.WeekScore;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/week-scores")
public class AdminWeekScoreController {

    private final WeekScoreRepository repository;

    public AdminWeekScoreController(WeekScoreRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {

        List<WeekScore> scores = repository.findAllByOrderByScoreDesc(PageRequest.of(0, 300));

        model.addAttribute("scores", scores);

        return "admin/week_scores";
    }

}
