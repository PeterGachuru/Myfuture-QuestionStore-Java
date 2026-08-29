package ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevel;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevelRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInviteeRepository;
import ke.co.myfuture.Myfuture.UserManagement.Post.PostRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDoneRepository;
import ke.co.myfuture.Myfuture.UserManagement.Referral.ReferralRepository;
import ke.co.myfuture.Myfuture.UserManagement.StudySubscription.StudySubscriptionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/read")
@AllArgsConstructor
public class StudentReadController {

    private final IbukaStudentAccountRepository ibukaStudentAccountRepository;
    private final CurriLevelRepository curriLevelRepository;
    private final SubjectLevelRepository subjectLevelRepository;

    private final QuizDoneRepository quizDoneRepository;
    private final ReferralRepository referralRepository;
    private final PostRepository postRepository;
    private final ContestInviteeRepository contestInviteeRepository;
    private final StudySubscriptionRepository studySubscriptionRepository;


    @GetMapping("/myprofile")
    public String myProfile(
            HttpServletRequest request,
            Model model
    ) {

        IbukaStudentAccount sessionStudent =
                (IbukaStudentAccount) request.getSession()
                        .getAttribute("student");

        if (sessionStudent == null || sessionStudent.getId() == null) {
            return "redirect:/read/students/select";
        }


        IbukaStudentAccount student =
                ibukaStudentAccountRepository
                        .findById(sessionStudent.getId())
                        .orElse(null);


        if (student == null) {
            request.getSession().removeAttribute("student");
            return "redirect:/read/students/select";
        }


        /*
         * Load the student's class level.
         */
        CurriLevel curriLevel =
                curriLevelRepository
                        .findById(student.getClasslevel())
                        .orElse(null);


        /*
         * Load all subjects available for this student's
         * class level.
         *
         * SubjectLevel connects a Subject to a CurriLevel.
         */
        List<SubjectLevel> subjectLevels =
                subjectLevelRepository
                        .findSubjectsForClass(student.getClasslevel());


        /*
         * STUDENT ACTIVITY
         */

        model.addAttribute(
                "quizzes",
                quizDoneRepository
                        .findByStudentOrderByCreatedAtDesc(student)
        );


        model.addAttribute(
                "referrals",
                referralRepository
                        .findByReferrerStudentId(student.getId())
        );


        model.addAttribute(
                "posts",
                postRepository
                        .findByStudentaccount(student)
        );


        model.addAttribute(
                "contests",
                contestInviteeRepository
                        .findByStudentaccount(student)
        );


        model.addAttribute(
                "subscriptions",
                studySubscriptionRepository
                        .findTop10ByEmailAddressOrderByCreatedAtDesc(
                                student.parentUsername
                        )
        );


        /*
         * PROFILE
         */

        model.addAttribute(
                "student",
                student
        );


        model.addAttribute(
                "curriLevel",
                curriLevel
        );


        /*
         * SUBJECTS AVAILABLE FOR QUIZZES
         */
        model.addAttribute(
                "subjectLevels",
                subjectLevels
        );


        return "read/myprofile";

    }

}