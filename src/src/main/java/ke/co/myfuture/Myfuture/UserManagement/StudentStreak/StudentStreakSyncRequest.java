package ke.co.myfuture.Myfuture.UserManagement.StudentStreak;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentStreakSyncRequest {
    private Long studentId;
    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDate lastActivityDate;
    private Boolean streakActiveToday;
}
