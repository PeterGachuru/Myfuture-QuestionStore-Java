package ke.co.myfuture.Myfuture.HttpAuth;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Role.RoleConfig;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserService;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
@Component
@RequiredArgsConstructor
public class AuthenticationManager implements org.springframework.security.authentication.AuthenticationManager {
    private final UserService userService;

    private final JWTUtil jwtUtil;

//    public AuthenticationManager(UserService userService, JWTUtil jwtUtil) {
//        this.userService = userService;
//        this.jwtUtil = jwtUtil;
//    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication.getPrincipal() != null) {
            String authToken = authentication.getPrincipal().toString();

            String email = jwtUtil.getEmailFromToken(authToken);

            System.out.println("User to validate: "+email);

            List<RoleConfig> roles = this.userService.validateUser(email);
            if (roles != null && !roles.isEmpty()) {
                log.log(Level.WARNING, String.format("Authenticated user roles [ %s ] ", roles));
                return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
                        authentication.getCredentials(),
                        roles.stream().map(RoleConfig::getAccessRights)
                                .toList().stream().flatMap(Collection::stream)
                                .toList().stream().map(s -> new SimpleGrantedAuthority(s.name())).distinct()
                                .collect(Collectors.toList()));
            } else {
               return authentication;
            }
        } else {
            log.log(Level.WARNING, String.format("Http validate auth no authenticate [ %s ]", authentication));
            return new UsernamePasswordAuthenticationToken("", "");
        }
    }
}
