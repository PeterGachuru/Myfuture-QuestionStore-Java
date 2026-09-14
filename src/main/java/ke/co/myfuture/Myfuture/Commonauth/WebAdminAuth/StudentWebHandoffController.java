package ke.co.myfuture.Myfuture.Commonauth.WebAdminAuth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestService;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.LoginSession;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/read")
public class StudentWebHandoffController {

    private final StudentWebHandoffService handoffService;
    private final UserRepository userRepository;
    private final UserUtil userUtil;
    private final IbukaStudentAccountRepository studentRepository;
    private final ContestService contestService;

    public StudentWebHandoffController(StudentWebHandoffService handoffService,
                                       UserRepository userRepository,
                                       UserUtil userUtil,
                                       IbukaStudentAccountRepository studentRepository,
                                       ContestService contestService) {
        this.handoffService = handoffService;
        this.userRepository = userRepository;
        this.userUtil = userUtil;
        this.studentRepository = studentRepository;
        this.contestService = contestService;
    }

    @GetMapping("/student-handoff")
    public String handoff(@RequestParam String token,
                          HttpServletRequest request) {
        try {
            Claims claims = handoffService.parse(token);
            Long studentId = ((Number) claims.get("studentId")).longValue();
            StudentWebHandoffService.Action action =
                    StudentWebHandoffService.Action.valueOf(
                            String.valueOf(claims.get("action"))
                    );

            IbukaStudentAccount student =
                    studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                return "redirect:/read/login?error=Student+not+found";
            }

            if (!student.getParentUsername().equalsIgnoreCase(claims.getSubject())
                    && !student.getParent().equals(
                    userRepository.findByEmail(claims.getSubject())
                            .map(u -> u.getId())
                            .orElse(-1L))) {
                return "redirect:/read/login?error=Invalid+student+handoff";
            }

            LoginSession loginSession =
                    userUtil.loginBuilder(
                            userUtil.getUserDetails(claims.getSubject()).getUser()
                    );

            HttpSession session = request.getSession(true);
            session.setAttribute("user", loginSession);
            session.setAttribute("student", student);
            session.setAttribute("studentAutoLoggedIn", true);

            Long resourceId = claims.get("resourceId") == null
                    ? null
                    : ((Number) claims.get("resourceId")).longValue();

            return switch (action) {
                case CREATE -> "redirect:/read/contests/create";
                case CONTESTS -> "redirect:/read/contests";
                case ATTEMPT -> {
                    if (resourceId == null || !contestService.studentMayAccessContest(studentId, resourceId)) {
                        yield "redirect:/read/contests?error=Contest+access+denied";
                    }
                    yield "redirect:/read/contests/" + resourceId + "/attempt";
                }
                case DETAILS -> {
                    if (resourceId == null || !contestService.studentMayAccessContest(studentId, resourceId)) {
                        yield "redirect:/read/contests?error=Contest+access+denied";
                    }
                    yield "redirect:/read/contests/" + resourceId + "/details";
                }
            };
        } catch (Exception e) {
            return "redirect:/read/login?error=The+link+has+expired";
        }
    }
}
