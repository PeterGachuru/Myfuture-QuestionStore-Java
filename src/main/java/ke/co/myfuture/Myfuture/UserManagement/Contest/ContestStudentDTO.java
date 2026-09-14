package ke.co.myfuture.Myfuture.UserManagement.Contest;

import lombok.Data;

import java.util.Date;

@Data
public class ContestStudentDTO {
    public Long contestId;
    public String creatorName;
    public String subjectName;
    public String classLevelName;
    public Integer questionCount;
    public Integer participantCount;
    public Date createdAt;
    public boolean attempted;
    public Integer score;
    public Integer overall;
    public Long inviteeId;

    public ContestStudentDTO(Contest contest, String subjectName, String classLevelName,
                             Integer questionCount, Integer participantCount,
                             boolean attempted, Integer score, Integer overall, Long inviteeId) {
        this.contestId = contest.id;
        this.creatorName = contest.creatorName;
        this.subjectName = subjectName;
        this.classLevelName = classLevelName;
        this.questionCount = questionCount;
        this.participantCount = participantCount;
        this.createdAt = contest.createdAt;
        this.attempted = attempted;
        this.score = score;
        this.overall = overall;
        this.inviteeId = inviteeId;
    }
}
