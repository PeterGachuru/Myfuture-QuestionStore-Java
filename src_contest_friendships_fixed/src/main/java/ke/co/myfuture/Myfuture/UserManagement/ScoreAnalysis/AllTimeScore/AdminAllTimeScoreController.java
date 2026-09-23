package ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.AllTimeScore;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelService;
import ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.TodayScore.TodayScore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/all-time-scores")
@AllArgsConstructor
public class AdminAllTimeScoreController {

    private final AllTimeScoreRepository repository;

    private final CurriLevelService curriLevelService;


    @GetMapping
    public String list(Model model) {

        List<AllTimeScore> scores = repository.findAllByOrderByScoreDesc(PageRequest.of(0, 300));

        for (AllTimeScore allTimeScore: scores) {
            allTimeScore.setClassLevel(curriLevelService.getById(allTimeScore.classLevelId).getName());
        }
        model.addAttribute("scores", scores);

        return "admin/all_time_scores";
    }

}