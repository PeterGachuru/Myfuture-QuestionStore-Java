package ke.co.myfuture.Myfuture.UserManagement.ReportsAndMarketing.WeeklyStudentReports;

import lombok.Data;

@Data
public class SubjectAnalysisDto {
    private Long subjectId;
    private String subjectName;
    private int totalScore;
    private double percentage;

    public SubjectAnalysisDto(Long subjectId, String subjectName, int totalScore, double percentage) {
        this.subjectId = subjectId;
        this.totalScore = totalScore;
        this.percentage = percentage;
        this.subjectName = subjectName;
    }
}