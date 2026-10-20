package ke.co.myfuture.Myfuture.UserManagement.QuizDone;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/quizzes-done")
public class AdminQuizDoneController {

    private final QuizDoneRepository repository;
    private final SubjectRepository subjectRepository;
    private final CurriLevelRepository curriLevelRepository;

    public AdminQuizDoneController(QuizDoneRepository repository, SubjectRepository subjectRepository,
                                   CurriLevelRepository curriLevelRepository) {
        this.repository = repository;
        this.subjectRepository = subjectRepository;
        this.curriLevelRepository = curriLevelRepository;
    }

    @GetMapping
    public String list(Model model) {

        List<QuizDone> quizzes =
                repository.findAllByOrderByCreatedAtDesc(PageRequest.of(0,300));

        model.addAttribute("quizzes", quizzes);

        Map<Long, String> subjectNames = subjectRepository.findAllById(
                        quizzes.stream().map(q -> q.subjectId).distinct().toList())
                .stream().collect(Collectors.toMap(Subject::getId,
                        subject -> label(subject.getName(), subject.getId())));
        Map<Long, String> classLevelNames = curriLevelRepository.findAllById(
                        quizzes.stream().filter(q -> q.student != null && q.student.getClasslevel() != null)
                                .map(q -> q.student.getClasslevel()).distinct().toList())
                .stream().collect(Collectors.toMap(CurriLevel::getId,
                        level -> label(level.getName(), level.getId())));

        model.addAttribute("subjectNames", subjectNames);
        model.addAttribute("classLevelNames", classLevelNames);

        return "admin/quizzes_done";
    }

    private static String label(String name, Long id) {
        return name == null || name.isBlank() ? "ID " + id : name + " (ID " + id + ")";
    }
}
