package ke.co.myfuture.Myfuture.Commonauth.WebAdminAuth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin-old")
public class AdminDashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/admin/login";
        }

        return "admin/dashboard";
    }

}