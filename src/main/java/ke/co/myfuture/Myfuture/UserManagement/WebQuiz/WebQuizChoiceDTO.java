package ke.co.myfuture.Myfuture.UserManagement.WebQuiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebQuizChoiceDTO {

    private Long id;

    private String value;

    private Boolean correct;
}