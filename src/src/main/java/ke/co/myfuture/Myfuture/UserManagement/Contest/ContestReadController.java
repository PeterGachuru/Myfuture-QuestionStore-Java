package ke.co.myfuture.Myfuture.UserManagement.Contest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestionRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevel;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevelRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInvitee;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.LoginSession;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInviteeRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contestquestion.ContestQuestionRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDone;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDoneRepository;
import ke.co.myfuture.Myfuture.UserManagement.WebQuiz.WebQuizService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/read/contests")
public class ContestReadController {

    private final ContestRepository contestRepository;
    private final ContestService contestService;
    private final ContestInviteeRepository inviteeRepository;
    private final ContestQuestionRepository contestQuestionRepository;
    private final CurriQuestionRepository questionRepository;
    private final CurriLevelRepository curriLevelRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectLevelRepository subjectLevelRepository;
    private final IbukaStudentAccountRepository studentRepository;
    private final QuizDoneRepository quizDoneRepository;
    private final WebQuizService webQuizService;

    public ContestReadController(ContestRepository contestRepository,
                                 ContestService contestService,
                                 ContestInviteeRepository inviteeRepository,
                                 ContestQuestionRepository contestQuestionRepository,
                                 CurriQuestionRepository questionRepository,
                                 CurriLevelRepository curriLevelRepository,
                                 SubjectRepository subjectRepository,
                                 SubjectLevelRepository subjectLevelRepository,
                                 IbukaStudentAccountRepository studentRepository,
                                 QuizDoneRepository quizDoneRepository,
                                 WebQuizService webQuizService) {
        this.contestRepository = contestRepository;
        this.contestService = contestService;
        this.inviteeRepository = inviteeRepository;
        this.contestQuestionRepository = contestQuestionRepository;
        this.questionRepository = questionRepository;
        this.curriLevelRepository = curriLevelRepository;
        this.subjectRepository = subjectRepository;
        this.subjectLevelRepository = subjectLevelRepository;
        this.studentRepository = studentRepository;
        this.quizDoneRepository = quizDoneRepository;
        this.webQuizService = webQuizService;
    }

    @GetMapping
    public String contests(HttpServletRequest request, Model model) {
        IbukaStudentAccount student = currentStudent(request);
        if (student == null) return "redirect:/read/students/select";

        List<ContestStudentDTO> pending = new ArrayList<>();
        List<ContestStudentDTO> completed = new ArrayList<>();
        Set<Long> seen = new HashSet<>();

        for (ContestInvitee invitee : inviteeRepository.findByStudentaccount(student)) {
            Contest contest = contestRepository.findById(invitee.getContest()).orElse(null);
            if (contest == null || !seen.add(contest.id)) continue;

            ContestStudentDTO dto = toStudentDTO(contest, invitee);
            if (dto.attempted) completed.add(dto);
            else pending.add(dto);
        }

        // Backwards compatibility for contests created before the creator
        // was automatically added as a contest invitee.
        for (Contest contest : contestRepository.findByCreator(student)) {
            if (!seen.add(contest.id)) continue;

            Optional<QuizDone> attempt =
                    quizDoneRepository.findFirstByContest_IdAndStudent_IdOrderByCreatedAtDesc(
                            contest.id, student.getId());

            ContestStudentDTO dto = toStudentDTO(
                    contest,
                    attempt.map(a -> {
                        ContestInvitee synthetic = new ContestInvitee();
                        synthetic.setContest(contest.id);
                        synthetic.setStudentaccount(student);
                        synthetic.setAttempted(a.getEndDate() != null);
                        synthetic.setScore(a.getScore());
                        return synthetic;
                    }).orElseGet(() -> {
                        ContestInvitee synthetic = new ContestInvitee();
                        synthetic.setContest(contest.id);
                        synthetic.setStudentaccount(student);
                        synthetic.setAttempted(false);
                        return synthetic;
                    })
            );

            if (dto.attempted) completed.add(dto);
            else pending.add(dto);
        }

        pending.sort(Comparator.comparing(ContestStudentDTO::getCreatedAt,
                Comparator.nullsLast(Comparator.reverseOrder())));
        completed.sort(Comparator.comparing(ContestStudentDTO::getCreatedAt,
                Comparator.nullsLast(Comparator.reverseOrder())));

        model.addAttribute("pendingContests", pending);
        model.addAttribute("completedContests", completed);
        model.addAttribute("student", student);
        consumeAutoLoginFlag(request, model);
        return "read/contests/index";
    }

    @GetMapping("/create")
    public String createPage(HttpServletRequest request, Model model) {
        IbukaStudentAccount student = currentStudent(request);
        if (student == null) return "redirect:/read/students/select";

        List<SubjectLevel> subjectLevels =
                subjectLevelRepository.findSubjectsForClass(student.getClasslevel());

        model.addAttribute("student", student);
        model.addAttribute("curriLevel",
                curriLevelRepository.findById(student.getClasslevel()).orElse(null));
        model.addAttribute("subjectLevels", subjectLevels);
        consumeAutoLoginFlag(request, model);
        return "read/contests/create";
    }

    @PostMapping("/create")
    public String createContest(@RequestParam Long subjectId,
                                @RequestParam(name = "inviteeIds", required = false) List<Long> inviteeIds,
                                HttpServletRequest request) {
        IbukaStudentAccount student = currentStudent(request);
        if (student == null) return "redirect:/read/students/select";

        ContestService.CreateContest create = new ContestService.CreateContest();
        create.setCreator_id(student.getId());
        create.setClasslevel_id(student.getClasslevel());
        create.setSubject_id(subjectId);
        // Questions are selected automatically by ContestService so participants
        // cannot choose or manipulate the contest questions from the browser.
        create.setInvitees(inviteeIds == null ? new ArrayList<>() : inviteeIds);
        create.setInvitees_count(create.getInvitees().size() + 1);

        Optional<Contest> saved = contestService.createContest(create);
        if (saved.isEmpty()) {
            return "redirect:/read/contests/create?error=Could+not+create+contest";
        }

        return "redirect:/read/contests/" + saved.get().id + "/details";
    }

    @GetMapping("/invitees")
    @ResponseBody
    public List<StudentOption> invitees(@RequestParam(defaultValue = "") String search,
                                        HttpServletRequest request) {
        IbukaStudentAccount student = currentStudent(request);
        if (student == null) return List.of();

        return studentRepository.contestInviteesExactClass(
                        search.trim(),
                        30,
                        student.getClasslevel(),
                        student.getId()
                )
                .stream()
                .map(s -> new StudentOption(s.id, s.getName(), s.getSchool()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{contestId}/attempt")
    public String attempt(@PathVariable Long contestId,
                          HttpServletRequest request) {
        IbukaStudentAccount student = currentStudent(request);
        if (student == null) return "redirect:/read/students/select";

        if (!contestService.studentMayAccessContest(student.getId(), contestId)) {
            return "redirect:/read/contests?error=Contest+access+denied";
        }

        try {
            QuizDone quiz = webQuizService.createContestQuiz(
                    student.getId(),
                    contestId
            );

            if (quiz.getEndDate() != null) {
                return "redirect:/read/contests/" + contestId + "/details";
            }

            return "redirect:/read/webquiz/play/" + quiz.id + "?question=1";
        } catch (RuntimeException e) {
            return "redirect:/read/contests?error=" +
                    java.net.URLEncoder.encode(
                            e.getMessage(),
                            java.nio.charset.StandardCharsets.UTF_8
                    );
        }
    }

    @GetMapping("/{contestId}/details")
    public String details(@PathVariable Long contestId,
                          HttpServletRequest request,
                          Model model) {
        IbukaStudentAccount student = currentStudent(request);
        if (student == null) return "redirect:/read/students/select";

        if (!contestService.studentMayAccessContest(student.getId(), contestId)) {
            return "redirect:/read/contests?error=Contest+access+denied";
        }

        Contest contest = contestRepository.findById(contestId).orElse(null);
        if (contest == null) return "redirect:/read/contests";

        List<ContestAttemptDTO> attempts = inviteeRepository
                .findByContestOrderByCreatedAtDesc(contestId)
                .stream()
                .filter(ContestInvitee::isAttempted)
                .map(invitee -> {
                    Optional<QuizDone> quiz = quizDoneRepository
                            .findFirstByContest_IdAndStudent_IdOrderByCreatedAtDesc(
                                    contestId,
                                    invitee.getStudentaccount().getId()
                            );

                    Integer overall = quiz.map(QuizDone::getOverall)
                            .orElse((int) contestQuestionRepository.countByContest(contestId));

                    return new ContestAttemptDTO(
                            invitee.getStudentaccount().getId(),
                            invitee.getStudentaccount().getName(),
                            invitee.getScore(),
                            overall,
                            invitee.getAttemptedAt()
                    );
                })
                .sorted(Comparator.comparing(
                        ContestAttemptDTO::getScore,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .collect(Collectors.toList());

        Optional<QuizDone> mine =
                contestService.getStudentContestAttempt(
                        contestId,
                        student.getId()
                );

        Subject subject = subjectRepository
                .findById(contest.subjectId)
                .orElse(null);

        CurriLevel level = curriLevelRepository
                .findById(contest.classlevelId)
                .orElse(null);

        int rank = 0;
        int index = 0;

        for (ContestAttemptDTO attempt : attempts) {
            index++;

            if (attempt.studentId.equals(student.getId())) {
                rank = index;
                break;
            }
        }

        double average = attempts.stream()
                .map(ContestAttemptDTO::getScore)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        model.addAttribute("contest", contest);
        model.addAttribute("subject", subject);
        model.addAttribute("curriLevel", level);
        model.addAttribute("attempts", attempts);
        model.addAttribute("myAttempt", mine.orElse(null));
        model.addAttribute("myRank", rank);
        model.addAttribute(
                "averageScore",
                Math.round(average * 10.0) / 10.0
        );
        model.addAttribute(
                "questionCount",
                contestQuestionRepository.countByContest(contestId)
        );
        model.addAttribute(
                "participantCount",
                inviteeRepository.countByContest(contestId)
        );
        model.addAttribute("student", student);
        consumeAutoLoginFlag(request, model);

        return "read/contests/details";
    }

    private ContestStudentDTO toStudentDTO(
            Contest contest,
            ContestInvitee invitee) {

        Subject subject = subjectRepository
                .findById(contest.subjectId)
                .orElse(null);

        CurriLevel level = curriLevelRepository
                .findById(contest.classlevelId)
                .orElse(null);

        Optional<QuizDone> latestAttempt =
                quizDoneRepository
                        .findFirstByContest_IdAndStudent_IdOrderByCreatedAtDesc(
                                contest.id,
                                invitee.getStudentaccount().getId()
                        );

        boolean attempted = invitee.isAttempted()
                || latestAttempt
                .map(q -> q.getEndDate() != null)
                .orElse(false);

        Integer score = latestAttempt
                .map(QuizDone::getScore)
                .orElse(invitee.getScore());

        Integer overall = latestAttempt
                .map(QuizDone::getOverall)
                .orElse(null);

        return new ContestStudentDTO(
                contest,
                subject == null ? "Unknown subject" : subject.getName(),
                level == null ? "Unknown class" : level.getName(),
                contestQuestionRepository.countByContest(contest.id),
                inviteeRepository.countByContest(contest.id),
                attempted,
                score,
                overall,
                invitee.id
        );
    }

    private IbukaStudentAccount currentStudent(
            HttpServletRequest request) {

        HttpSession session = request.getSession();

        LoginSession user =
                (LoginSession) session.getAttribute("user");

        if (user == null) return null;

        IbukaStudentAccount sessionStudent =
                (IbukaStudentAccount) session.getAttribute("student");

        if (sessionStudent == null || sessionStudent.getId() == null) {
            return null;
        }

        IbukaStudentAccount student =
                studentRepository
                        .findById(sessionStudent.getId())
                        .orElse(null);

        if (student == null ||
                !user.getUserId().equals(student.getParent())) {

            session.removeAttribute("student");
            return null;
        }

        return student;
    }

    private void consumeAutoLoginFlag(
            HttpServletRequest request,
            Model model) {

        Boolean auto =
                (Boolean) request.getSession()
                        .getAttribute("studentAutoLoggedIn");

        if (Boolean.TRUE.equals(auto)) {
            model.addAttribute("studentAutoLoggedIn", true);

            request.getSession()
                    .removeAttribute("studentAutoLoggedIn");
        }
    }

    public record StudentOption(Long id, String name, String school) {}
}
