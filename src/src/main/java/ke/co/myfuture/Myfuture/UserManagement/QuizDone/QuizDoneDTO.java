package ke.co.myfuture.Myfuture.UserManagement.QuizDone;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contest;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import lombok.Data;

import java.util.Date;

@Data
public class QuizDoneDTO {
    public Long id;
    public Long inid;
    public Long installId;
    public Integer questionsCount;
    public Integer appVersion;
    public String category;
    public IbukaStudentAccount student;
    public Contest contest;
    public Date startDate;
    public Date endDate;
    public Long subjectId;
    public Subject subject;
    public Integer score;
    public Integer overall;
    public Boolean deleted;
    public Date createdAt;
    public Date updatedAt;
    public CurriLevel curriLevel;

    public QuizDoneDTO(QuizDone quizDone, Subject subject, CurriLevel curriLevel) {
        this.id = quizDone.id;
        this.inid = quizDone.inid;
        this.installId = quizDone.installId;
        this.questionsCount = quizDone.questionsCount;
        this.category = quizDone.category;
        this.student = quizDone.student;
        this.contest = quizDone.contest;
        this.appVersion = quizDone.appVersion;
        this.startDate = quizDone.startDate;
        this.endDate = quizDone.endDate;
        this.subjectId = quizDone.subjectId;
        this.subject = subject;
        this.score = quizDone.score;
        this.overall = quizDone.overall;
        this.deleted = quizDone.deleted;
        this.createdAt = quizDone.createdAt;
        this.updatedAt = quizDone.updatedAt;
        this.curriLevel = curriLevel;
    }
}
