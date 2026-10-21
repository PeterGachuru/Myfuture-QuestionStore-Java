package ke.co.myfuture.Myfuture.UserManagement.StudentStreak;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class StudentStreakService {
    private static final ZoneId APP_ZONE = ZoneId.of("Africa/Nairobi");

    private final StudentStreakRepository streakRepository;
    private final IbukaStudentAccountRepository studentRepository;

    public StudentStreakService(StudentStreakRepository streakRepository,
                                IbukaStudentAccountRepository studentRepository) {
        this.streakRepository = streakRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public StudentStreak sync(StudentStreakSyncRequest request) {
        if (request.getStudentId() == null || request.getStudentId() <= 0) {
            throw new IllegalArgumentException("A valid studentId is required");
        }
        int incomingCurrent = nonNegative(request.getCurrentStreak());
        int incomingLongest = Math.max(incomingCurrent, nonNegative(request.getLongestStreak()));

        IbukaStudentAccount student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student was not found"));
        StudentStreak streak = streakRepository.findByStudent_Id(student.getId())
                .orElseGet(StudentStreak::new);

        LocalDate incomingDate = request.getLastActivityDate();
        LocalDate storedDate = streak.getLastActivityDate();
        if (storedDate == null || (incomingDate != null && incomingDate.isAfter(storedDate))) {
            streak.setLastActivityDate(incomingDate);
            streak.setCurrentStreak(incomingDate == null ? 0 : incomingCurrent);
        } else if (incomingDate != null && incomingDate.equals(storedDate)) {
            streak.setCurrentStreak(Math.max(nonNegative(streak.getCurrentStreak()), incomingCurrent));
        }

        streak.setLongestStreak(Math.max(nonNegative(streak.getLongestStreak()), incomingLongest));
        streak.setStudent(student);
        LocalDate today = LocalDate.now(APP_ZONE);
        LocalDate lastActivity = streak.getLastActivityDate();
        boolean activeToday = lastActivity != null && lastActivity.equals(today);
        boolean streakStillCurrent = lastActivity != null && !lastActivity.isBefore(today.minusDays(1));
        if (!streakStillCurrent) streak.setCurrentStreak(0);
        streak.setStreakActiveToday(activeToday);
        return streakRepository.save(streak);
    }

    private int nonNegative(Integer value) {
        return value == null ? 0 : Math.max(0, value);
    }
}
