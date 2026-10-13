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
//    if (session.getAttribute("user") == null) {
//        return "redirect:/admin/login";
//    }

        LocalDateTime startDateTime = LocalDateTime.now().minusMonths(3);

        Date startDate = Date.from(
                startDateTime.atZone(ZoneId.systemDefault()).toInstant()
        );

        List<GraphData> graphs = new ArrayList<>();

        long start, end;

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "studentsChart",
                "Student Registrations",
                ibukaStudentAccountRepository.countPerDay(startDate),
                "Date",
                "Students"
        ));
        end = System.currentTimeMillis();
        System.out.println("studentsChart generated in " + (end - start) + " ms");

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "accountsAddedPerDay",
                "Accounts Added",
                installRepository.accountsAddedPerDay(startDate),
                "Date",
                "Users"
        ));
        end = System.currentTimeMillis();
        System.out.println("accountsAddedPerDay generated in " + (end - start) + " ms");

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "referralsChart",
                "Referrals",
                referralRepository.countPerDay(startDate),
                "Date",
                "Referrals"
        ));
        end = System.currentTimeMillis();
        System.out.println("referralsChart generated in " + (end - start) + " ms");

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "quizChart",
                "Quizzes Done",
                quizDoneRepository.countPerDay(startDate),
                "Date",
                "Quizzes"
        ));
        end = System.currentTimeMillis();
        System.out.println("quizChart generated in " + (end - start) + " ms");

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "activeStudentsChart",
                "Active Students (DAU)",
                quizDoneRepository.countActiveStudentsPerDay(startDate),
                "Date",
                "Active Users"
        ));
        end = System.currentTimeMillis();
        System.out.println("activeStudentsChart generated in " + (end - start) + " ms");

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "contestAttemptsChart",
                "Contest Attempts",
                contestInviteeRepository.countAttemptsPerDay(startDate),
                "Date",
                "Contest Attempts"
        ));
        end = System.currentTimeMillis();
        System.out.println("contestAttemptsChart generated in " + (end - start) + " ms");

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "postAttemptsChart",
                "Post Attempts",
                postattemptRepository.countAttemptsPerDay(startDate),
                "Date",
                "Post Attempts"
        ));
        end = System.currentTimeMillis();
        System.out.println("postAttemptsChart generated in " + (end - start) + " ms");

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "transactionsChart",
                "Transactions",
                studySubscriptionRepository.countTransactionsPerDay(startDate),
                "Date",
                "Transactions"
        ));
        end = System.currentTimeMillis();
        System.out.println("transactionsChart generated in " + (end - start) + " ms");

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "revenueChart",
                "Revenue",
                studySubscriptionRepository.sumTransactionsPerDay(startDate),
                "Date",
                "Amount (KES)"
        ));
        end = System.currentTimeMillis();
        System.out.println("revenueChart generated in " + (end - start) + " ms");

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "visitsChart",
                "Page Visits",
                pageVisitRepository.countPerDay(startDateTime),
                "Date",
                "Visits"
        ));
        end = System.currentTimeMillis();
        System.out.println("visitsChart generated in " + (end - start) + " ms");

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "chatChart",
                "Chat Messages",
                chatmessageRepository.countPerDay(startDate),
                "Date",
                "Messages"
        ));
        end = System.currentTimeMillis();
        System.out.println("chatChart generated in " + (end - start) + " ms");

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "postsChart",
                "Posts Created",
                postRepository.countPerDay(startDate),
                "Date",
                "Posts"
        ));
        end = System.currentTimeMillis();
        System.out.println("postsChart generated in " + (end - start) + " ms");

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "contestChart",
                "Contests Created",
                contestRepository.countPerDay(startDate),
                "Date",
                "Contests"
        ));
        end = System.currentTimeMillis();
        System.out.println("contestChart generated in " + (end - start) + " ms");

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "installsChart",
                "Total Installs",
                installRepository.countInstallsPerDay(startDate),
                "Date",
                "Installs"
        ));
        end = System.currentTimeMillis();
        System.out.println("installsChart generated in " + (end - start) + " ms");

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "installsWithAccountsChart",
                "Installs (With Account)",
                installRepository.countInstallsWithAccountsPerDay(startDate),
                "Date",
                "Users"
        ));
        end = System.currentTimeMillis();
        System.out.println("installsWithAccountsChart generated in " + (end - start) + " ms");

        start = System.currentTimeMillis();
        graphs.add(graphService.fromCountPerDay(
                "conversionChart",
                "Account Conversion (%)",
                installRepository.accountConversionPerDay(startDate),
                "Date",
                "Percentage (%)"
        ));
        end = System.currentTimeMillis();
        System.out.println("conversionChart generated in " + (end - start) + " ms");

        for (GraphData graphData : graphs) {
            System.out.println("id: " + graphData.getId());
        }

        model.addAttribute("graphs", graphs);

        return "admin/dashboard";
    }
}
