package ke.co.myfuture.Myfuture.UserManagement.ReportsAndMarketing.WeeklyStudentReports;

import lombok.Data;

import java.util.List;

@Data
public class WeeklyAnalysisDto {
    private int totalWeekScore;
    private List<SubjectAnalysisDto> subjects;

    public WeeklyAnalysisDto(int totalWeekScore, List<SubjectAnalysisDto> subjects) {
        this.totalWeekScore = totalWeekScore;
        this.subjects = subjects;
    }
}
