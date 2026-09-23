package ke.co.myfuture.Myfuture.UserManagement.Contest;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;

import java.util.Date;

public class ContestSummaryDTO {
    public Long id;
    public String creatorName;
    public String subjectName;
    public String classLevelName;
    public int invitesCount;
    public int numberOfQuestions;
    public Date createdAt;

    public ContestSummaryDTO(Contest contest, Subject subject, CurriLevel level) {
        this.id = contest.id;
        this.creatorName = contest.creatorName;
        this.subjectName = subject != null ? subject.getName() : "N/A";
        this.classLevelName = level != null ? level.getName() : "N/A";
        this.invitesCount = contest.invitesCount;
        this.numberOfQuestions = contest.contestQuestions != null ? contest.contestQuestions.size() : 0;
        this.createdAt = contest.createdAt;
    }
}
