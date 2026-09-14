package ke.co.myfuture.Myfuture.UserManagement.Contest;

import lombok.Data;

import java.util.Date;

@Data
public class ContestAttemptDTO {
    public Long studentId;
    public String studentName;
    public Integer score;
    public Integer overall;
    public Date attemptedAt;

    public ContestAttemptDTO(Long studentId, String studentName,
                             Integer score, Integer overall, Date attemptedAt) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.score = score;
        this.overall = overall;
        this.attemptedAt = attemptedAt;
    }
}
