package ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/students")
public class AdminStudentController {

    private final IbukaStudentAccountRepository repository;

    public AdminStudentController(IbukaStudentAccountRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {

        List<IbukaStudentAccount> students =
                repository.findAllByOrderByIdDesc(PageRequest.of(0,300));

        model.addAttribute("students", students);

        return "admin/students";
    }

}