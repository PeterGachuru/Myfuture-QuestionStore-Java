package ke.co.myfuture.Myfuture.Commonauth.Auth.User;


import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@AllArgsConstructor
public class AdminWebUsersController {
    private final UserRepository userRepository;
    @GetMapping("/users")
    public String listUsers(Model model) {

        List<User> users =
                userRepository.findByDeletedDateIsNullOrderByIdDesc(PageRequest.of(0,300));

        model.addAttribute("users", users);

        return "admin/users";
    }
}
