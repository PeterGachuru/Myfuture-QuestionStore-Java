package ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.WeekScore;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelService;
import ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.TodayScore.TodayScore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/week-scores")
@AllArgsConstructor
public class AdminWeekScoreController {

    private final WeekScoreRepository repository;

    private final CurriLevelService curriLevelService;


    @GetMapping
    public String list(Model model) {

        List<WeekScore> scores = repository.findAllByOrderByScoreDesc(PageRequest.of(0, 300));

        for (WeekScore weekScore: scores) {
            weekScore.setClassLevel(curriLevelService.getById(weekScore.classLevelId).getName());
        }

        model.addAttribute("scores", scores);

        return "admin/week_scores";
    }

}
