package ke.co.myfuture.Myfuture.Commonauth.WebAdminAuth;

import jakarta.servlet.http.HttpServletResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.LoginResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserService;
import ke.co.myfuture.Myfuture.Commonauth.RememberMeToken.RememberMeService;
import ke.co.myfuture.Myfuture.HttpAuth.CookieService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelService;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminAuthController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final RememberMeService rememberMeService;
    private final CurriLevelService curriLevelService;
    private final IbukaStudentAccountRepository ibukaStudentAccountRepository;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/admin/dashboard";
        }

        return "admin/login";
    }
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session, HttpServletResponse httpServletResponse) {

        LoginResponse response =
                userService.authenticateUser(email, password);
        String rememberToken = rememberMeService.createToken(response.getUser().getId());

        CookieService.addRememberMeCookie(httpServletResponse, rememberToken);

        if (response.getStatusCode() == 200) {

            session.setAttribute("user", response.getUser());

            return "redirect:/admin/dashboard";
        }

        model.addAttribute("error", response.getMessage());

        return "admin/login";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {

        List<User> users =
                userRepository.findByDeletedDateIsNullOrderByIdDesc(PageRequest.of(0,300));

        model.addAttribute("users", users);

        return "admin/users";
    }

    @GetMapping("/users/profile")
    public String userProfile(@RequestParam String email, Model model) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        List<IbukaStudentAccount> students =
                ibukaStudentAccountRepository.findByParentUsernameOrderByIdDesc(user.getEmail());

        for (IbukaStudentAccount student : students) {
            if (student.getClasslevel() != null) {
                student.setCurriLevel(
                        curriLevelService.getById(student.getClasslevel())
                );
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("students", students);
        model.addAttribute("studentCount", students.size());

        return "admin/user-profile";
    }
}
