package ke.co.myfuture.Myfuture.UserManagement.Contest;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails.SchedulerService;
import ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails.SenderService;
import ke.co.myfuture.Myfuture.UserManagement.Cgroup.Cgroup;
import ke.co.myfuture.Myfuture.UserManagement.Cgroup.CgroupService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInvitee;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInviteeRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contestquestion.ContestQuestion;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contestquestion.ContestQuestionRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    CgroupService cgroupService;


    public Optional<Contest> createContest(CreateContest createContest) {
        Contest contest = new Contest();
        Optional<IbukaStudentAccount> creator = ibukaStudentAccountRepository.findById(createContest.creator_id);
        if (creator.isEmpty()) return null;
        Optional<Subject> subject = subjectRepository.findById(createContest.getSubject_id());
        if (subject.isEmpty()) return null;
        Optional<CurriLevel> curriLevel = curriLevelRepository.findById(createContest.getClasslevel_id());
        if (curriLevel.isEmpty()) return null;
        contest.creator = creator.get();
        contest.creatorName = creator.get().getName();
        contest.classlevelId = createContest.classlevel_id;
        contest.invitesCount = createContest.invitees_count;
        contest.subjectId = createContest.subject_id;

        Cgroup cgroup = new Cgroup();
        cgroup.setType("Many");
        cgroup.setDescription("Question group");
        cgroup.setName("Question group");

        cgroup = cgroupService.newCgroup(cgroup);
        contest.cgroup = cgroup.id;


        Optional<IbukaStudentAccount> ibukaStudentAccount;
        List<ContestInvitee> contestInvitees = new ArrayList<>();
        for (Long inviteeId: createContest.invitees) {
            ContestInvitee contestInvitee = new ContestInvitee();
            ibukaStudentAccount = ibukaStudentAccountRepository.findById(inviteeId);
            if (ibukaStudentAccount.isEmpty()) return null;
            contestInvitee.setStudentaccount(ibukaStudentAccount.get());

            contestInvitees.add(contestInvitee);
        }
        List<ContestQuestion> contestQuestions = new ArrayList<>();
        ContestQuestion contestQuestion;
        for (Long questionId: createContest.questions) {
            contestQuestion = new ContestQuestion();
            contestQuestion.setQuestion(questionId);
            contestQuestions.add(contestQuestion);
        }

        Contest savedContest = repository.save(contest);
        for (ContestInvitee contestInvitee: contestInvitees) {
            contestInvitee.setContest(savedContest.id);
        }
        for (ContestQuestion question: contestQuestions) {
            question.setContest(savedContest.id);
        }

        contestQuestionRepository.saveAll(contestQuestions);
        contestInviteeRepository.saveAll(contestInvitees);

        emailInvitees(savedContest, contestInvitees, subject.get(), curriLevel.get());

        return repository.findById(savedContest.id);
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

            parentUser = userRepository.findById(contestInvitee.getStudentaccount().getParent()).get();
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
    static class CreateContest {
        Long  creator_id;
        Long  classlevel_id;
        Long  subject_id;
        Integer  invitees_count;
        String  creator_name;

        List<Long> invitees;
        List<Long> questions;
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