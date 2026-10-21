package ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount;


import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestionRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contest;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInviteeRepository;
import ke.co.myfuture.Myfuture.UserManagement.Feedbacks.Rating.RatingRepository;
import ke.co.myfuture.Myfuture.UserManagement.Post.PostRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDoneRepository;
import ke.co.myfuture.Myfuture.UserManagement.Referral.ReferralRepository;
import ke.co.myfuture.Myfuture.UserManagement.StudySubscription.StudySubscriptionRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDone;
import ke.co.myfuture.Myfuture.UserManagement.Post.Post;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

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
    private final SubjectRepository subjectRepository;
    private final ContestRepository contestRepository;
    private final CurriQuestionRepository curriQuestionRepository;

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
        List<QuizDone> quizzes = quizDoneRepository.findByStudentOrderByCreatedAtDesc(student);
        List<Post> posts = postRepository.findByStudentaccount(student);
        List<ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInvitee> contests =
                contestInviteeRepository.findByStudentaccount(student);

        Set<Long> subjectIds = new HashSet<>();
        for (QuizDone quiz : quizzes) {
            if (quiz.getSubjectId() != null) subjectIds.add(quiz.getSubjectId());
        }

        List<Long> contestIds = contests.stream()
                .map(invite -> invite.getContest())
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Long> contestSubjectIds = new HashMap<>();
        if (!contestIds.isEmpty()) {
            for (Contest contest : contestRepository.findAllById(contestIds)) {
                contestSubjectIds.put(contest.getId(), contest.getSubjectId());
                if (contest.getSubjectId() != null) subjectIds.add(contest.getSubjectId());
            }
        }

        Map<Long, String> subjectNames = new HashMap<>();
        if (!subjectIds.isEmpty()) {
            for (Subject subject : subjectRepository.findAllById(subjectIds)) {
                subjectNames.put(subject.getId(), subject.getName());
            }
        }

        Map<Long, String> postTopicNames = new HashMap<>();
        List<Long> questionIds = posts.stream()
                .map(Post::getQuestionid)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (!questionIds.isEmpty()) {
            for (CurriQuestionRepository.QuestionTopicView topic : curriQuestionRepository.findTopicNamesByQuestionIds(questionIds)) {
                postTopicNames.put(topic.getQuestionId(), topic.getTopicName());
            }
        }

        model.addAttribute("quizzes", quizzes);
        model.addAttribute("subjectNames", subjectNames);
        Map<Long, String> contestSubjectNames = new HashMap<>();
        contestSubjectIds.forEach((contestId, subjectId) -> contestSubjectNames.put(contestId,
                subjectNames.getOrDefault(subjectId, "Unknown subject (ID " + subjectId + ")")));
        model.addAttribute("contestSubjectNames", contestSubjectNames);
        model.addAttribute("referrals", referralRepository.findByReferrerStudentId(student.getId()));
        model.addAttribute("ratings", ratingRepository.findByIbukaStudentAccount(student));
        model.addAttribute("posts", posts);
        model.addAttribute("postTopicNames", postTopicNames);
        model.addAttribute("contests", contests);
        model.addAttribute("subscriptions", studySubscriptionRepository.findTop10ByEmailAddressOrderByCreatedAtDesc(student.parentUsername));

        return "admin/student";
    }
}
