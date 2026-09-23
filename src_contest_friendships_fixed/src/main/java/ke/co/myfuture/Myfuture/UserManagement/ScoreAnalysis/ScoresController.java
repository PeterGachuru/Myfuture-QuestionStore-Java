package ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis;

import ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.AllTimeScore.AllTimeScore;
import ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.AllTimeScore.AllTimeScoreRepository;
import ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.TodayScore.TodayScore;
import ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.TodayScore.TodayScoreRepository;
import ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.WeekScore.WeekScore;
import ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.WeekScore.WeekScoreRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("allscores")
public class ScoresController {
    @Autowired
    WeekScoreRepository weekScoreRepository;

    @Autowired
    TodayScoreRepository todayScoreRepository;

    @Autowired
    AllTimeScoreRepository allTimeScoreRepository;
    @GetMapping("all")
    public ResponseEntity<?> fetchScores(@RequestParam("studentId") Long studentId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("StudentAccount retrieved Successfully");
        ScoresHolder scoresHolder = new ScoresHolder();
        scoresHolder.all_time_score = allTimeScoreRepository.findAll();
        scoresHolder.week_score = weekScoreRepository.findAll();
        scoresHolder.today_score = todayScoreRepository.findAll();
        response.setEntity(scoresHolder);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Data
    class ScoresHolder {
        List<TodayScore> today_score;
        List<WeekScore> week_score;
        List<AllTimeScore> all_time_score;
    }
}
