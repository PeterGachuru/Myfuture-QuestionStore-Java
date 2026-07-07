package ke.co.myfuture.Myfuture.UserManagement.AdminDashboard;

import ke.co.myfuture.Myfuture.Commonauth.Install.Install2Repository;
import ke.co.myfuture.Myfuture.UserManagement.Chatmessage.ChatmessageRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInviteeRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import ke.co.myfuture.Myfuture.UserManagement.PageVisit.PageVisitRepository;
import ke.co.myfuture.Myfuture.UserManagement.Post.PostRepository;
import ke.co.myfuture.Myfuture.UserManagement.Post.Postatempt.PostattemptRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDoneRepository;
import ke.co.myfuture.Myfuture.UserManagement.Referral.ReferralRepository;
import ke.co.myfuture.Myfuture.UserManagement.StudySubscription.StudySubscriptionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminDashboard {
    private final GraphService graphService;
    private final IbukaStudentAccountRepository ibukaStudentAccountRepository;
    private final ReferralRepository referralRepository;
    private final QuizDoneRepository quizDoneRepository;
    private final PageVisitRepository pageVisitRepository;
    private final ChatmessageRepository chatmessageRepository;
    private final PostRepository postRepository;
    private final ContestRepository contestRepository;
    private final ContestInviteeRepository contestInviteeRepository;
    private final PostattemptRepository postattemptRepository;
    private final StudySubscriptionRepository studySubscriptionRepository;
    private final Install2Repository installRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
//        if (session.getAttribute("user") == null) {
//            return "redirect:/admin/login";
//        }

//        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);

        LocalDateTime startDateTime = LocalDateTime.now().minusMonths(3);

        Date startDate = Date.from(
                startDateTime.atZone(ZoneId.systemDefault()).toInstant()
        );

        ibukaStudentAccountRepository.countPerDay(startDate);

        List<GraphData> graphs = new ArrayList<>();

        graphs.add(graphService.fromCountPerDay(
                "studentsChart",
                "Student Registrations",
                ibukaStudentAccountRepository.countPerDay(startDate),
                "Date",
                "Students"
        ));

        graphs.add(graphService.fromCountPerDay(
                "accountsAddedPerDay",
                "Accounts Added",
                installRepository.accountsAddedPerDay(startDate),
                "Date",
                "Users"
        ));

        graphs.add(graphService.fromCountPerDay(
                "referralsChart",
                "Referrals",
                referralRepository.countPerDay(startDate),
                "Date",
                "Referrals"
        ));

        graphs.add(graphService.fromCountPerDay(
                "quizChart",
                "Quizzes Done",
                quizDoneRepository.countPerDay(startDate),
                "Date",
                "Quizzes"
        ));

        graphs.add(graphService.fromCountPerDay(
                "activeStudentsChart",
                "Active Students (DAU)",
                quizDoneRepository.countActiveStudentsPerDay(startDate),
                "Date",
                "Active Users"
        ));

        graphs.add(graphService.fromCountPerDay(
                "contestAttemptsChart",
                "Contest Attempts",
                contestInviteeRepository.countAttemptsPerDay(startDate),
                "Date",
                "Contest Attempts"
        ));

        graphs.add(graphService.fromCountPerDay(
                "postAttemptsChart",
                "Post Attempts",
                postattemptRepository.countAttemptsPerDay(startDate),
                "Date",
                "Post Attempts"
        ));

        graphs.add(graphService.fromCountPerDay(
                "transactionsChart",
                "Transactions",
                studySubscriptionRepository.countTransactionsPerDay(startDate),
                "Date",
                "Transactions"
        ));

        graphs.add(graphService.fromCountPerDay(
                "revenueChart",
                "Revenue",
                studySubscriptionRepository.sumTransactionsPerDay(startDate),
                "Date",
                "Amount (KES)"
        ));

        graphs.add(graphService.fromCountPerDay(
                "visitsChart",
                "Page Visits",
                pageVisitRepository.countPerDay(startDateTime),
                "Date",
                "Visits"
        ));

        graphs.add(graphService.fromCountPerDay(
                "chatChart",
                "Chat Messages",
                chatmessageRepository.countPerDay(startDate),
                "Date",
                "Messages"
        ));

        graphs.add(graphService.fromCountPerDay(
                "postsChart",
                "Posts Created",
                postRepository.countPerDay(startDate),
                "Date",
                "Posts"
        ));

        graphs.add(graphService.fromCountPerDay(
                "contestChart",
                "Contests Created",
                contestRepository.countPerDay(startDate),
                "Date",
                "Contests"
        ));

        graphs.add(graphService.fromCountPerDay(
                "installsChart",
                "Total Installs",
                installRepository.countInstallsPerDay(startDate),
                "Date",
                "Installs"
        ));

        graphs.add(graphService.fromCountPerDay(
                "installsWithAccountsChart",
                "Installs (With Account)",
                installRepository.countInstallsWithAccountsPerDay(startDate),
                "Date",
                "Users"
        ));

        graphs.add(graphService.fromCountPerDay(
                "conversionChart",
                "Account Conversion (%)",
                installRepository.accountConversionPerDay(startDate),
                "Date",
                "Percentage (%)"
        ));

        for (GraphData graphDataa: graphs)
            System.out.println("id: "+graphDataa.getId());

        model.addAttribute("graphs", graphs);

        return "admin/dashboard";
    }
}
