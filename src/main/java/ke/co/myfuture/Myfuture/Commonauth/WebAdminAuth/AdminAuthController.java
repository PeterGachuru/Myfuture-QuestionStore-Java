package ke.co.myfuture.Myfuture.Commonauth.WebAdminAuth;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.LoginResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    private final UserService userService;

    public AdminAuthController(UserService userService) {
        this.userService = userService;
    }

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
            HttpSession session) {

        LoginResponse response =
                userService.authenticateUser(email, password);

        if (response.getStatusCode() == 200) {

            session.setAttribute("user", response.getUser());

            return "redirect:/admin/dashboard";
        }

        model.addAttribute("error", response.getMessage());

        return "admin/login";
    }
}
