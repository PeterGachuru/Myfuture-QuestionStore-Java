package ke.co.myfuture.Myfuture.UserManagement.Contest;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails.SchedulerService;
import ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails.SenderService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestionRepository;
import ke.co.myfuture.Myfuture.UserManagement.Cgroup.Cgroup;
import ke.co.myfuture.Myfuture.UserManagement.Cgroup.CgroupService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestion;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInvitee;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInviteeRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contestquestion.ContestQuestion;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contestquestion.ContestQuestionRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDone;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDoneRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContestService {
    @Autowired
    ContestRepository repository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SchedulerService schedulerService;

    @Autowired
    IbukaStudentAccountRepository ibukaStudentAccountRepository;

    @Autowired
    ContestQuestionRepository contestQuestionRepository;
    @Autowired
    ContestInviteeRepository contestInviteeRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    CurriLevelRepository curriLevelRepository;

    @Autowired
    CurriQuestionRepository curriQuestionRepository;

    @Autowired
    CgroupService cgroupService;

    @Autowired
    QuizDoneRepository quizDoneRepository;


    public Optional<Contest> createContest(CreateContest createContest) {
        if (createContest == null || createContest.creator_id == null
                || createContest.classlevel_id == null || createContest.subject_id == null) {
            return Optional.empty();
        }

        Optional<IbukaStudentAccount> creatorOpt =
                ibukaStudentAccountRepository.findById(createContest.creator_id);
        if (creatorOpt.isEmpty()) return Optional.empty();

        IbukaStudentAccount creator = creatorOpt.get();

        // A contest can only be created for the creator's own class level.
        if (!creator.getClasslevel().equals(createContest.classlevel_id)) {
            return Optional.empty();
        }

        Optional<Subject> subject = subjectRepository.findById(createContest.getSubject_id());
        Optional<CurriLevel> curriLevel = curriLevelRepository.findById(createContest.getClasslevel_id());
        if (subject.isEmpty() || curriLevel.isEmpty()) return Optional.empty();

        /*
         * Select the contest questions on the server.
         *
         * Questions are grouped by subtopic, each group is shuffled, then
         * questions are picked round-robin. This prevents a contest from being
         * dominated by one subtopic when enough questions exist in other
         * subtopics, while still allowing all available questions to be used
         * when there are fewer than 20.
         */
        List<CurriQuestion> validQuestions =
                curriQuestionRepository.findQuizQuestions(
                        createContest.classlevel_id,
                        createContest.subject_id
                );

        Map<Long, List<CurriQuestion>> questionsBySubtopic = validQuestions.stream()
                .filter(q -> q.getSubtopic() != null && q.getSubtopic().getId() != null)
                .collect(Collectors.groupingBy(q -> q.getSubtopic().getId()));

        List<List<CurriQuestion>> subtopicQuestions = new ArrayList<>(questionsBySubtopic.values());
        subtopicQuestions.removeIf(List::isEmpty);

        if (subtopicQuestions.isEmpty()) return Optional.empty();

        // Randomise the order within every subtopic and the order of subtopics.
        Collections.shuffle(subtopicQuestions);
        subtopicQuestions.forEach(Collections::shuffle);

        List<Long> questionIds = new ArrayList<>(Math.min(20, validQuestions.size()));

        // Round-robin gives each available subtopic a chance before a
        // subtopic contributes its second/third/etc. question.
        int index = 0;
        while (questionIds.size() < 20 && !subtopicQuestions.isEmpty()) {
            if (index >= subtopicQuestions.size()) index = 0;

            List<CurriQuestion> subtopic = subtopicQuestions.get(index);
            if (!subtopic.isEmpty()) {
                questionIds.add(subtopic.remove(subtopic.size() - 1).getId());
            }

            if (subtopic.isEmpty()) {
                subtopicQuestions.remove(index);
            } else {
                index++;
            }
        }

        if (questionIds.isEmpty()) return Optional.empty();

        Contest contest = new Contest();
        contest.creator = creator;
        contest.creatorName = creator.getName();
        contest.classlevelId = createContest.classlevel_id;
        contest.subjectId = createContest.subject_id;
        contest.invitesCount = createContest.invitees == null
                ? 1
                : createContest.invitees.size() + 1;

        Cgroup cgroup = new Cgroup();
        cgroup.setType("Many");
        cgroup.setDescription("Contest Question Group");
        cgroup.setName("Contest Question Group");
        cgroup = cgroupService.newCgroup(cgroup);
        contest.cgroup = cgroup.id;

        Contest savedContest = repository.save(contest);

        List<ContestInvitee> contestInvitees = new ArrayList<>();

        // The creator is always a participant.
        ContestInvitee creatorInvitee = new ContestInvitee();
        creatorInvitee.setContest(savedContest.id);
        creatorInvitee.setStudentaccount(creator);
        creatorInvitee.setSelfInvited(true);
        contestInvitees.add(creatorInvitee);

        if (createContest.invitees != null) {
            for (Long inviteeId : createContest.invitees.stream().distinct().toList()) {
                if (inviteeId == null || inviteeId.equals(creator.getId())) continue;

                Optional<IbukaStudentAccount> inviteeOpt =
                        ibukaStudentAccountRepository.findById(inviteeId);
                if (inviteeOpt.isEmpty()) continue;

                // Contest invitations are restricted to the same class level.
                if (!creator.getClasslevel().equals(inviteeOpt.get().getClasslevel())) continue;

                ContestInvitee invitee = new ContestInvitee();
                invitee.setContest(savedContest.id);
                invitee.setStudentaccount(inviteeOpt.get());
                invitee.setSelfInvited(false);
                contestInvitees.add(invitee);
            }
        }

        savedContest.invitesCount = contestInvitees.size();
        savedContest = repository.save(savedContest);

        List<ContestQuestion> contestQuestions = new ArrayList<>();
        for (Long questionId : questionIds) {
            ContestQuestion question = new ContestQuestion();
            question.setContest(savedContest.id);
            question.setQuestion(questionId);
            contestQuestions.add(question);
        }

        contestQuestionRepository.saveAll(contestQuestions);
        contestInviteeRepository.saveAll(contestInvitees);

        emailInvitees(
                savedContest,
                contestInvitees.stream()
                        .filter(i -> !i.isSelfInvited())
                        .collect(Collectors.toList()),
                subject.get(),
                curriLevel.get()
        );

        return repository.findById(savedContest.id);
    }

    public boolean studentBelongsToAuthenticatedUser(Long studentId) {
        if (studentId == null) return false;
        User user = UserRequestContext.getcurrentUser();
        if (user == null) return false;

        return ibukaStudentAccountRepository.findById(studentId)
                .map(student -> user.getId().equals(student.getParent()))
                .orElse(false);
    }

    public boolean studentMayAccessContest(Long studentId, Long contestId) {
        if (studentId == null || contestId == null) return false;
        Optional<IbukaStudentAccount> student = ibukaStudentAccountRepository.findById(studentId);
        if (student.isEmpty()) return false;

        if (contestInviteeRepository
                .findByContestAndStudentaccount(contestId, student.get())
                .isPresent()) {
            return true;
        }

        return repository.findById(contestId)
                .map(contest -> contest.getCreator() != null
                        && contest.getCreator().getId().equals(studentId))
                .orElse(false);
    }

    public Optional<Contest> getContest(Long contestId) {
        return repository.findById(contestId);
    }

    public List<QuizDone> getContestAttempts(Long contestId) {
        return quizDoneRepository.findByContest_IdOrderByScoreDescEndDateAsc(contestId)
                .stream()
                .filter(q -> q.endDate != null)
                .collect(Collectors.toList());
    }

    public Optional<QuizDone> getStudentContestAttempt(Long contestId, Long studentId) {
        return quizDoneRepository.findFirstByContest_IdAndStudent_IdOrderByCreatedAtDesc(contestId, studentId)
                .filter(q -> q.endDate != null);
    }


    @Async
    private void emailInvitees(Contest savedContest, List<ContestInvitee> contestInvitees, Subject subject, CurriLevel curriLevel) {
        String inviteeEmailBodyContent;
        int count = 0;
        String emailSubject = String.format("Myfuture CBC Contest Invite: %s - %s", subject.getName(), curriLevel.getName());

        User parentUser;
        for (ContestInvitee contestInvitee: contestInvitees) {
            count++;
            inviteeEmailBodyContent = createContestInviteBody(contestInvitee.getStudentaccount().getName(),
                    savedContest.creatorName, subject.getName(), curriLevel.getName());

            Optional<User> parentUserOptional =
                    userRepository.findById(contestInvitee.getStudentaccount().getParent());

            if (parentUserOptional.isEmpty()) {
                System.out.println(
                        "Skipping contest invitation email: parent user not found for student "
                                + contestInvitee.getStudentaccount().getId()
                                + ", parent="
                                + contestInvitee.getStudentaccount().getParent()
                );
                continue;
            }

            parentUser = parentUserOptional.get();
            schedulerService.persistScheduledEmail(parentUser.getEmail(), emailSubject, inviteeEmailBodyContent,
                    "Ibuka Technologies", LocalDateTime.now().plusMinutes(count/5), SenderService.Broadcast);
        }
    }

    public Boolean saveScores(ScoresParentHolder scoresParentHolder) {
        System.out.println(scoresParentHolder);
        for (ScoresParentHolder.Attempt attempt: scoresParentHolder.getAttempts()) {
            contestInviteeRepository.updateScore(attempt.contestId, attempt.studentId, attempt.score);
        }
        return true;
    }

    public List<Contest> findAll() {
        return null;
    }


    @Data
    public static class CreateContest {
        Long  creator_id;
        Long  classlevel_id;
        Long  subject_id;
        Integer  invitees_count;
        String  creator_name;

        List<Long> invitees;
    }

    @Data
    static public class ScoresParentHolder {
        Long parentId;

        List<Attempt> attempts;

        @Data
        static public class Attempt {
            Long contestId;
            Integer score;
            Long studentId;
        }
    }

    public String createContestInviteBody(String inviteeName, String invitorName, String subject, String gradeLevel) {
        String uniqueToken = java.util.UUID.randomUUID().toString().substring(0, 8); // Prevent quoting
        return """
        <html>
            <body style="font-family: Verdana, sans-serif; line-height: 1.6; color: #222;">
                <table style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ccc; border-radius: 10px;">
                    <tr>
                        <td>
                            <h2 style="color: #1a73e8;">You're Invited to a Learning Challenge!</h2>
                            <p>Hello <strong>%s</strong>,</p>
                            <p>We’re excited to let you know that <strong>%s</strong> has sent you a special invite to test your skills in <strong>%s</strong> (<strong>%s</strong>).</p>
                            <p>Tap below to jump into the fun and learning:</p>
                            <div style="text-align: center; margin: 25px 0;">
                                <a href="https://play.google.com/store/apps/details?id=ke.co.myfuture"
                                   style="display: inline-block; background-color: #34a853; color: white; padding: 12px 24px; font-size: 16px; text-decoration: none; border-radius: 5px;">
                                   Launch the App
                                </a>
                            </div>
                            <p>Wishing you an exciting learning experience!</p>
                            <hr style="border: none; border-top: 1px solid #eee;">
                            <p style="font-size: 0.85em; color: #999;">Invite ID: %s — Myfuture CBC Revision App</p>
                        </td>
                    </tr>
                </table>
            </body>
        </html>
    """.formatted(inviteeName, invitorName, subject, gradeLevel, uniqueToken);
    }

    public List<ContestSummaryDTO> findAllContestSummaries() {
        // Load all Subjects and CurriLevels into maps for fast access
        Map<Long, Subject> subjectMap = subjectRepository.findAll().stream()
                .collect(Collectors.toMap(Subject::getId, s -> s));

        Map<Long, CurriLevel> levelMap = curriLevelRepository.findAll().stream()
                .collect(Collectors.toMap(CurriLevel::getId, c -> c));

        // Load contests
        List<Contest> contests = repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        // Map to DTO
        return contests.stream()
                .map(contest -> {
                    Subject subject = subjectMap.get(contest.subjectId);
                    CurriLevel level = levelMap.get(contest.classlevelId);
                    return new ContestSummaryDTO(contest, subject, level);
                })
                .collect(Collectors.toList());
    }
}