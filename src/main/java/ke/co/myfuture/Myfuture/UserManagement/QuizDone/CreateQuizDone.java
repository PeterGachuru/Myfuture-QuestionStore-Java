package ke.co.myfuture.Myfuture.UserManagement.QuizDone;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class CreateQuizDone {
    public Long id;
    public Long inid;
    public Long installId;
    public Integer appVersion;
    public Integer questionsCount;

    public Long studentId;

    public Long contestId;

    public Date startDate;

    public Date endDate;

    public Long subjectId;

    public String category;

    public  Integer score;
    public  Integer overall;
    public Boolean deleted;

    public List<CreateQuizQuestion> questions;

    @Data
    static class CreateQuizQuestion {
        public Integer numbering;
        public Long questionId;
        public Long selectedChoice;
        public Boolean gotCorrect;
        public String choicesOrder;
    }
}
