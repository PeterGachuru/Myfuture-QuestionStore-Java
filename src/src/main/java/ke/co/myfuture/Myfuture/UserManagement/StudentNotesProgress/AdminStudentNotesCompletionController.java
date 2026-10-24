package ke.co.myfuture.Myfuture.UserManagement.StudentNotesProgress;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/student-notes-progress")
public class AdminStudentNotesCompletionController {
    private final StudentNotesCompletionRepository repository;

    public AdminStudentNotesCompletionController(StudentNotesCompletionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Transactional(readOnly = true, transactionManager = "studentManagementTransactionManager")
    public String list(Model model) {
        List<Map<String, Object>> completions = repository.findTop1000ByOrderByCompletedAtDesc()
                .stream()
                .map(completion -> {
                    var student = completion.getStudent();
                    return Map.<String, Object>of(
                            "studentId", student.getId(),
                            "studentName", safe(student.getName()),
                            "classLevel", student.getClasslevel() == null ? "-" : student.getClasslevel(),
                            "notesId", completion.getNotesId(),
                            "subtopic", completion.getSubtopicId(),
                            "notesUpdatedAt", safe(completion.getNotesUpdatedAt()),
                            "completedAt", completion.getCompletedAt() == null ? "-" : completion.getCompletedAt()
                    );
                })
                .toList();
        model.addAttribute("completions", completions);
        model.addAttribute("completionCount", completions.size());
        return "admin/student_notes_progress";
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
