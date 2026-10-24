package ke.co.myfuture.Myfuture.UserManagement.StudentQuestion;
import java.util.Date;
public record StudentQuestionDTO(Long id, String question, String studentName, String className,
                                Integer gradeNumber, Long groupId, Long senderId, Long studentId, Date createdAt) {
    static StudentQuestionDTO from(StudentQuestion q, String className, Integer gradeNumber) {
        return new StudentQuestionDTO(q.getId(), q.getQuestion(), q.getStudentaccount().getName(), className,
                gradeNumber, q.getCgroup().getId(), q.getStudentaccount().getSender().getId(),
                q.getStudentaccount().getId(), q.getCreatedAt());
    }
}
