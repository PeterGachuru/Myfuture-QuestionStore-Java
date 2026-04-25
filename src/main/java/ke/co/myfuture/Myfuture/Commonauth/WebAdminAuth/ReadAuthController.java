package ke.co.myfuture.Myfuture.Commonauth.WebAdminAuth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.AuthEntityResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.LoginResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.LoginSession;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.UserCreateRequest;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.PasswordReset.PasswordResetDTO;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.PasswordReset.PasswordResetService;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserService;
import ke.co.myfuture.Myfuture.Commonauth.Install.Install;
import ke.co.myfuture.Myfuture.Commonauth.Install.WebInstallService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/read")
@AllArgsConstructor
public class ReadAuthController {
    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final IbukaStudentAccountRepository ibukaStudentAccountRepository;
    private final CurriLevelRepository curriLevelRepository;

    private final WebInstallService installService;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {

        if (session.getAttribute("user") != null) {
            return "redirect:/read/students/select";
        }

        return "read/login";
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

            return "redirect:/read/students/select";
        }

        model.addAttribute("error", response.getMessage());

        return "read/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate(); // 🔥 kills session
        return "redirect:/read"; // or wherever you want
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "read/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email, Model model) {
        PasswordResetDTO dto = new PasswordResetDTO();
        dto.setEmail(email);
        dto.setCreationDate(new Timestamp(System.currentTimeMillis()));

        UniversalResponse response = passwordResetService.passwordResetRequest(dto);

        model.addAttribute("message", response.getMessage());

        return "read/enter-otp";
    }


    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserCreateRequest());
        return "read/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") UserCreateRequest userRequest,
            Model model) {

        if (userRequest.getEmail() == null || !userRequest.getEmail().contains("@")) {
            model.addAttribute("error", "Invalid email address");
            return "read/register";
        }

        AuthEntityResponse response = userService.createUser(userRequest);

        if (response.getStatusCode() == 200) {
            model.addAttribute("success", "Account created successfully. Please login.");
            return "read/login";
        }

        model.addAttribute("error", response.getMessage());
        return "read/register";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword,
            Model model) {

        UniversalResponse response =
                passwordResetService.resetPasswordWithOtp(email, otp, newPassword);

        if (response.getStatusCode() == 200) {
            model.addAttribute("success", response.getMessage());
            return "read/login";
        }

        model.addAttribute("error", response.getMessage());
        return "read/enter-otp";
    }

    @GetMapping("/students/select")
    public String selectStudent(HttpServletRequest request, Model model, HttpServletResponse response) {
        System.out.println("Logged user: "+request.getSession().getAttribute("user"));
        LoginSession user = (LoginSession) request.getSession().getAttribute("user");
        System.out.println("LoginSession: "+user);
        if (user == null) {
            return "redirect:/read/login";
        }


        Install install = installService.getOrCreateInstall(request, response);

        model.addAttribute("installId", install.getId());

        List<IbukaStudentAccount> students =
                ibukaStudentAccountRepository.findByParent(user.getUserId());

        students.forEach(s -> {
            curriLevelRepository.findById(s.getClasslevel())
                    .ifPresent(s::setCurriLevel);
        });

        System.out.println("Students: "+students);

        model.addAttribute("students", students);
        model.addAttribute("classLevels", curriLevelRepository.findByCurriculumNotOrderByCurriculumAscNumberingAsc(1L));

        return "read/selectstudent";
    }

    @PostMapping("/students/select")
    public String selectStudentPost(@RequestParam Long studentId,
                                    HttpServletRequest request) {

        IbukaStudentAccount student =
                ibukaStudentAccountRepository.findById(studentId).orElse(null);

        if (student != null) {
            request.getSession().setAttribute("student", student);
        }

        Optional<CurriLevel> curriLevel = curriLevelRepository.findById(student.getClasslevel());

        return "redirect:/read/classlevel/"+curriLevel.get().getSlug(); // or previous page  /read/classlevel/{slug}
    }


    @PostMapping("/students/create")
    public String createStudent(@RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam(required = false) String school,
                                @RequestParam Long classlevel,
                                HttpServletRequest request,
                                HttpServletResponse response) {

        LoginSession user = (LoginSession) request.getSession().getAttribute("user");

        Install install = installService.getOrCreateInstall(request, response);

        System.out.println("User: "+user);

        System.out.println("User Id: "+user.getId());

        IbukaStudentAccount student = new IbukaStudentAccount();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setName(firstName + " " + lastName);
        student.setSchool(school);
        student.setInstallId(install.getId());
        student.setClasslevel(classlevel);
        student.setParent(user.getUserId());
        student.setParentUsername(user.getEmail());
        student.setTotalScore(0L);

        ibukaStudentAccountRepository.save(student);

        request.getSession().setAttribute("student", student);

        return "redirect:/";
    }

    @GetMapping("/subscribe")
    public String subscribePage(HttpSession session, Model model) {

        if (session.getAttribute("user") == null) {
            return "redirect:/read/students/select";
        }

        model.addAttribute("plans", List.of(
                Map.of("name","2 Days","days",2,"price",10),
                Map.of("name","7 Days","days",7,"price",29),
                Map.of("name","30 Days","days",30,"price",99),
                Map.of("name","90 Days","days",90,"price",250),
                Map.of("name","365 Days","days",365,"price",800)
        ));

        return "read/subscribe";
    }
}
