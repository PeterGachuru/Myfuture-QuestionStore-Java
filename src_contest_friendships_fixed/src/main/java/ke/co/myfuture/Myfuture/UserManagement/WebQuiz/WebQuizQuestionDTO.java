package ke.co.myfuture.Myfuture.UserManagement.WebQuiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebQuizQuestionDTO {

    private Long quizQuestionId;

    private Long questionId;

    private int position;

    private String question;

    private List<WebQuizChoiceDTO> choices;

    private Long selectedChoice;

    private Boolean answered;

    private Boolean gotCorrect;

    private String explanation;
}