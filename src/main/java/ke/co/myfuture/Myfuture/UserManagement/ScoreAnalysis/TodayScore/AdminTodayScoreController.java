package ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.TodayScore;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/today-scores")
public class AdminTodayScoreController {

    private final TodayScoreRepository repository;

    public AdminTodayScoreController(TodayScoreRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {

        List<TodayScore> scores = repository.findAllByOrderByScoreDesc(PageRequest.of(0, 300));

        model.addAttribute("scores", scores);

        return "admin/today_scores";
    }

}