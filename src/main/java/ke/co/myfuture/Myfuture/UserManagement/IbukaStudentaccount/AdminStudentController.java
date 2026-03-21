package ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount;

import com.lowagie.text.pdf.PRIndirectReference;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelService;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInviteeRepository;
import ke.co.myfuture.Myfuture.UserManagement.Feedbacks.Rating.RatingRepository;
import ke.co.myfuture.Myfuture.UserManagement.Post.PostRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDoneRepository;
import ke.co.myfuture.Myfuture.UserManagement.Referral.ReferralRepository;
import ke.co.myfuture.Myfuture.UserManagement.StudySubscription.StudySubscriptionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/students")
@AllArgsConstructor
public class AdminStudentController {

    private final IbukaStudentAccountRepository repository;
    private final QuizDoneRepository quizDoneRepository;
    private final ReferralRepository referralRepository;
    private final RatingRepository ratingRepository;
    private final PostRepository postRepository;
    private final ContestInviteeRepository contestInviteeRepository;
    private final StudySubscriptionRepository studySubscriptionRepository;
    private final CurriLevelService curriLevelService;

    @GetMapping
    public String list(Model model) {

        List<IbukaStudentAccount> students =
                repository.findAllByOrderByIdDesc(PageRequest.of(0,300));

        for (IbukaStudentAccount ibukaStudentAccount: students) {
            ibukaStudentAccount.setCurriLevel(curriLevelService.getById(ibukaStudentAccount.classlevel));
        }

        model.addAttribute("students", students);

        return "admin/students";
    }

    @GetMapping("/{id}")
    public String student(@PathVariable Long id, Model model) {
        IbukaStudentAccount student = repository.findById(id).orElseThrow();

        model.addAttribute("student", student);
        model.addAttribute("quizzes", quizDoneRepository.findByStudentOrderByCreatedAtDesc(student));
        model.addAttribute("referrals", referralRepository.findByReferrerStudentId(student.getId()));
        model.addAttribute("ratings", ratingRepository.findByIbukaStudentAccount(student));
        model.addAttribute("posts", postRepository.findByStudentaccount(student));
        model.addAttribute("contests", contestInviteeRepository.findByStudentaccount(student));
        model.addAttribute("subscriptions", studySubscriptionRepository.findTop10ByEmailAddressOrderByCreatedAtDesc(student.parentUsername));

        return "admin/student";
    }
}