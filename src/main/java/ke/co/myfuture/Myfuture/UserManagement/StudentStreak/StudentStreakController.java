package ke.co.myfuture.Myfuture.UserManagement.StudentStreak;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("student-streak")
public class StudentStreakController {
    private final StudentStreakService service;

    public StudentStreakController(StudentStreakService service) {
        this.service = service;
    }

    @PostMapping("/sync")
    public ResponseEntity<?> sync(@RequestBody StudentStreakSyncRequest request) {
        try {
            StudentStreak saved = service.sync(request);
            return ResponseEntity.ok(Map.of(
                    "status", "Success",
                    "message", "Student streak synchronized",
                    "studentId", saved.getStudent().getId(),
                    "currentStreak", saved.getCurrentStreak(),
                    "longestStreak", saved.getLongestStreak()
            ));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "Error", "message", exception.getMessage()));
        }
    }
}
