package ke.co.myfuture.Myfuture.UserManagement.StudentQuestion;
import lombok.Data;
@Data
public class StudentQuestionRequest {
    private String clientQuestionId;
    private Long studentId;
    private Long installId;
    private Long studentInId;
    private Long parentId;
    private String question;
}
