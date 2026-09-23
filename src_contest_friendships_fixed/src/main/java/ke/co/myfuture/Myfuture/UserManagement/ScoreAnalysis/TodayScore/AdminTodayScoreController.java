package ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.TodayScore;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/today-scores")
@AllArgsConstructor
public class AdminTodayScoreController {
    private final TodayScoreRepository repository;
    private final CurriLevelService curriLevelService;

    @GetMapping
    public String list(Model model) {

        List<TodayScore> scores = repository.findAllByOrderByScoreDesc(PageRequest.of(0, 300));

        for (TodayScore todayScore: scores) {
            todayScore.setClassLevel(curriLevelService.getById(todayScore.classLevelId).getName());
        }

        model.addAttribute("scores", scores);

        return "admin/today_scores";
    }
}