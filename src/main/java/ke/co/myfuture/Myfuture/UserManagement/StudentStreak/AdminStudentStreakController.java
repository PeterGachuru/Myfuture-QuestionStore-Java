package ke.co.myfuture.Myfuture.UserManagement.StudentStreak;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({"/admin/streaks", "/admin/student-streaks"})
public class AdminStudentStreakController {
    private static final ZoneId APP_ZONE = ZoneId.of("Africa/Nairobi");
    private final StudentStreakRepository repository;

    public AdminStudentStreakController(StudentStreakRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Transactional(readOnly = true, transactionManager = "studentManagementTransactionManager")
    public String list(Model model) {
        LocalDate today = LocalDate.now(APP_ZONE);
        List<Map<String, Object>> streaks = repository.findTop500ByOrderByUpdatedAtDesc()
                .stream()
                .map(streak -> {
                    LocalDate lastActivity = streak.getLastActivityDate();
                    boolean activeToday = lastActivity != null && lastActivity.equals(today);
                    int current = lastActivity != null && !lastActivity.isBefore(today.minusDays(1))
                            ? streak.getCurrentStreak() : 0;
                    return Map.<String, Object>of(
                            "studentId", streak.getStudent().getId(),
                            "studentName", safe(streak.getStudent().getName()),
                            "classLevel", streak.getStudent().getClasslevel() == null ? "-" : streak.getStudent().getClasslevel(),
                            "currentStreak", current,
                            "longestStreak", streak.getLongestStreak(),
                            "lastActivityDate", lastActivity == null ? "-" : lastActivity,
                            "activeToday", activeToday,
                            "updatedAt", streak.getUpdatedAt() == null ? "-" : streak.getUpdatedAt()
                    );
                })
                .toList();
        model.addAttribute("streaks", streaks);
        model.addAttribute("activeTodayCount", streaks.stream()
                .filter(row -> Boolean.TRUE.equals(row.get("activeToday"))).count());
        return "admin/student_streaks";
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "Student" : value;
    }
}
