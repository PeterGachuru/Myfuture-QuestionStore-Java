package ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount;

import lombok.Data;

import java.time.Instant;

@Data
public class StudentScoreEventRequest {
    private String eventId;
    private Long studentId;
    private Long scoreDelta;
    private String source;
    private Long installId;
    private Long studentInId;
    private Instant occurredAt;
}
