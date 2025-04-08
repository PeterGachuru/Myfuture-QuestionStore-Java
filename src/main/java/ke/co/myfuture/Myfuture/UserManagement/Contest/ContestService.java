package ke.co.myfuture.Myfuture.UserManagement.Contest;

import ke.co.myfuture.Myfuture.QuestionStore.Cgroup.Cgroup;
import ke.co.myfuture.Myfuture.QuestionStore.Cgroup.CgroupService;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInvitee;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInviteeRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contestquestion.ContestQuestion;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contestquestion.ContestQuestionRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContestService {
    @Autowired
    ContestRepository repository;

    @Autowired
    IbukaStudentAccountRepository ibukaStudentAccountRepository;

    @Autowired
    ContestQuestionRepository contestQuestionRepository;
    @Autowired
    ContestInviteeRepository contestInviteeRepository;

    @Autowired
    CgroupService cgroupService;


    public Optional<Contest> createContest(CreateContest createContest) {
        Contest contest = new Contest();
        Optional<IbukaStudentAccount> creator = ibukaStudentAccountRepository.findById(createContest.creator_id);
        if (creator.isEmpty()) return null;
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
        for (ContestInvitee contestInvitee: contestInvitees){
            contestInvitee.setContest(savedContest.id);
        }
        for (ContestQuestion question: contestQuestions) {
            question.setContest(savedContest.id);
        }

        contestQuestionRepository.saveAll(contestQuestions);
        contestInviteeRepository.saveAll(contestInvitees);

        return repository.findById(savedContest.id);
    }

    public Boolean saveScores(ScoresParentHolder scoresParentHolder) {
        System.out.println(scoresParentHolder);
        for (ScoresParentHolder.Attempt attempt: scoresParentHolder.getAttempts()) {
            contestInviteeRepository.updateScore(attempt.contestId, attempt.studentId, attempt.score);
        }
        return true;
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
//    {"parent_id":2,"attempts":[{"contest":2,"score":6,"student_id":1}]}
}