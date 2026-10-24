package ke.co.myfuture.Myfuture.UserManagement.StudentNotesProgress;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("student-notes-progress")
public class StudentNotesCompletionController {
    private final StudentNotesCompletionService service;

    public StudentNotesCompletionController(StudentNotesCompletionService service) {
        this.service = service;
    }

    @PostMapping("/complete")
    public ResponseEntity<?> complete(@RequestBody StudentNotesCompletionRequest request) {
        try {
            StudentNotesCompletion saved = service.complete(request);
            return ResponseEntity.ok(Map.of(
                    "status", "Success",
                    "completed", true,
                    "studentId", saved.getStudent().getId(),
                    "notesId", saved.getNotesId(),
                    "subtopicId", saved.getSubtopicId(),
                    "notesUpdatedAt", saved.getNotesUpdatedAt(),
                    "completedAt", saved.getCompletedAt().toString()
            ));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "Error", "message", exception.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> status(
            @RequestParam Long studentId,
            @RequestParam Long notesId,
            @RequestParam String notesUpdatedAt
    ) {
        return ResponseEntity.ok(Map.of(
                "status", "Success",
                "completed", service.isComplete(studentId, notesId, notesUpdatedAt)
        ));
    }
}
