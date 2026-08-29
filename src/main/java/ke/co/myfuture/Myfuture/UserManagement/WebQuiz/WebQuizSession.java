package ke.co.myfuture.Myfuture.UserManagement.WebQuiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebQuizSession {

    private Long quizId;

    private Long studentId;

    private Long classLevelId;

    private Long subjectId;

    private List<Long> questionIds;

    private Integer currentQuestion;

    private Date startDate;
}