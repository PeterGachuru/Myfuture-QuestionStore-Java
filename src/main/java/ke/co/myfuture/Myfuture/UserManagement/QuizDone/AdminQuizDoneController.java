package ke.co.myfuture.Myfuture.UserManagement.QuizDone;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/quizzes-done")
public class AdminQuizDoneController {

    private final QuizDoneRepository repository;

    public AdminQuizDoneController(QuizDoneRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {

        List<QuizDone> quizzes =
                repository.findAllByOrderByCreatedAtDesc(PageRequest.of(0,300));

        model.addAttribute("quizzes", quizzes);

        return "admin/quizzes_done";
    }
}
