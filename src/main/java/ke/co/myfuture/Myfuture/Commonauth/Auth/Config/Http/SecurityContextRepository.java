package ke.co.myfuture.Myfuture.Commonauth.Auth.Config.Http;


import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserService;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.JWTUtil;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.logging.Level;

@Log
@Component
//@AllArgsConstructor
public class SecurityContextRepository implements org.springframework.security.web.context.SecurityContextRepository {
    private AuthenticationManager authenticationManager;

    private static final String EMPTY_CREDENTIALS = "";
    private static final String ANONYMOUS_USER = "anonymousUser";

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserService userService;

    private static String email;

//    private final String cookieHmacKey;

//    public SecurityContextRepository(@Value("${auth.cookie.hmac-key}") String cookieHmacKey) {
//        this.cookieHmacKey = cookieHmacKey;
//    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();


        log.log(Level.INFO, String.format("REQUEST HOLDER %s : ", requestResponseHolder));

        String authHeader = String.valueOf(request.getHeaders(HttpHeaders.AUTHORIZATION));

        log.log(Level.INFO, String.format("REQUEST : %s", request));

        log.log(Level.INFO, String.format("Authorization Token : %s", authHeader));

        String authToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authToken = authHeader.substring(7);

            log.log(Level.WARNING, "Bearer token found and is valid.");
        } else {
            log.log(Level.WARNING, "Couldn't find bearer string, will ignore the header.");
        }

        if (authToken != null) {
            Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken);

            log.log(Level.WARNING, String.format("Auth [ %s ] ", auth));

            email = jwtUtil.getEmailFromToken(authToken);

            return (SecurityContext) this.authenticationManager.authenticate(auth);
        } else {
            return SecurityContextHolder.getContext();
        }
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return false;
    }


    public Optional<User> currentUser() {
        return this.userService.getLoggedInUser(email);
    }
}
