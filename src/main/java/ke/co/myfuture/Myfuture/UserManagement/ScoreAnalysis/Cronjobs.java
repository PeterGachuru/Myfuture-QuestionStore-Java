package ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis;

import ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.AllTimeScore.AllTimeScoreRepository;
import ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.TodayScore.TodayScoreRepository;
import ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.WeekScore.WeekScoreRepository;
import ke.co.myfuture.Myfuture.UserManagement.Studentaccount.StudentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Cronjobs {
    @Autowired
    StudentAccountRepository studentAccountRepository;
    @Autowired
    WeekScoreRepository weekScoreRepository;

    @Autowired
    TodayScoreRepository todayScoreRepository;

    @Autowired
    AllTimeScoreRepository allTimeScoreRepository;
    @Scheduled(fixedDelay = 3600000, initialDelay = 10)
    void analyzeScores() {
        studentAccountRepository.analyzeScores();

        System.out.println("Analyze for week");
        weekScoreRepository.clearEveryThing();
        weekScoreRepository.analyzeWeekScore();

        System.out.println("Analyze for today");
        todayScoreRepository.clearEveryThing();
        todayScoreRepository.analyzeScore();

        System.out.println("Analyze for all time");
        allTimeScoreRepository.clearEveryThing();
        allTimeScoreRepository.analyzeScore();
    }
}
