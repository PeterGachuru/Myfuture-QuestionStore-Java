package ke.co.myfuture.Myfuture.UserManagement.StudentNotesProgress;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentNotesCompletionRequest {
    private Long studentId;
    private Long notesId;
    private Long subtopicId;
    private String notesUpdatedAt;
    private String completedAt;
}
