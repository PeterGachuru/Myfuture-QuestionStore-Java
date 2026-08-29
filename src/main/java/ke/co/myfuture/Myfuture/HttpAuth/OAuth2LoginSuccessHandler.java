package ke.co.myfuture.Myfuture.HttpAuth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.LoginSession;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserService;
import ke.co.myfuture.Myfuture.Commonauth.RememberMeToken.RememberMeService;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private RememberMeService rememberMeService;

    @Autowired
    private UserService userService;

    @Autowired
    private IbukaStudentAccountRepository ibukaStudentAccountRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauthUser =
                (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");

        LoginSession loginSession =
                userService.loginResponse(email)
                        .get()
                        .getUser();

        request.getSession().setAttribute("user", loginSession);

        String rememberToken =
                rememberMeService.createToken(loginSession.getUserId());

        cookieService.addRememberMeCookie(response, rememberToken);


        // Check if this parent already has a student
        boolean hasStudents =
                ibukaStudentAccountRepository
                        .existsByParent(loginSession.getUserId());


        if (hasStudents) {

            // Existing parent → select a student
            setDefaultTargetUrl("/read/students/select");

        } else {

            // New parent → create first student
            setDefaultTargetUrl("/read/students/create");

        }

        setAlwaysUseDefaultTargetUrl(true);

        super.onAuthenticationSuccess(
                request,
                response,
                authentication
        );
    }
}