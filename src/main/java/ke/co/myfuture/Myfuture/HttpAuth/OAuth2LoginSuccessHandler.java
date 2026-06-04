package ke.co.myfuture.Myfuture.HttpAuth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.LoginSession;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserService;
import ke.co.myfuture.Myfuture.Commonauth.RememberMeToken.RememberMeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    CookieService cookieService;

    @Autowired
    RememberMeService rememberMeService;

    @Autowired
    UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        LoginSession loginSession = userService.loginResponse(email).get().getUser();

        request.getSession().setAttribute("user", loginSession );

        String rememberToken = rememberMeService.createToken(loginSession.getUserId());

        cookieService.addRememberMeCookie(response, rememberToken);

        setDefaultTargetUrl("/read/students/select");
        setAlwaysUseDefaultTargetUrl(true);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}