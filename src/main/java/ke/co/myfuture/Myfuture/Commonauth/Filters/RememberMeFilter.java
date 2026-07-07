package ke.co.myfuture.Myfuture.Commonauth.Filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.LoginSession;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.UserData;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserUtil;
import ke.co.myfuture.Myfuture.Commonauth.RememberMeToken.RememberMeService;
import ke.co.myfuture.Myfuture.Commonauth.RememberMeToken.RememberMeToken;
import ke.co.myfuture.Myfuture.HttpAuth.CookieService;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class RememberMeFilter extends OncePerRequestFilter {

    @Autowired
    private RememberMeService rememberMeService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IbukaStudentAccountRepository ibukaStudentAccountRepository;
    @Autowired
    private UserUtil userUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
//        System.out.println("===========In RememberMeFilter==========");
        LoginSession sessionUser =
                (LoginSession) request.getSession().getAttribute("user");

        if (sessionUser == null) {

            String token = CookieService.getRememberMeCookie(request);

//            System.out.println("Remember me token: "+token);

            if (token != null) {
                Optional<RememberMeToken> userIdOpt = rememberMeService.validateToken(token);

                if (userIdOpt.isPresent()) {

                    User user = userRepository.findById(userIdOpt.get().getUserId()).orElse(null);

                    if (user != null) {
                        UserData userData = userUtil.getUserDetails(user);

                        LoginSession loginSession = userUtil.loginBuilder(userData);

                        request.getSession().setAttribute("user", loginSession);
                        if (userIdOpt.get().getStudentId() != null) {
                            request.getSession().setAttribute("student", ibukaStudentAccountRepository.findById(userIdOpt.get().getStudentId()).get());
                        }
                        System.out.println("Restored login");
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}