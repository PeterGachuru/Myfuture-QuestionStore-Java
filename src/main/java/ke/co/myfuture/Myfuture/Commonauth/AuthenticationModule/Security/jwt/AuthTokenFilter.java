package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.User.UserData;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserService;
import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.utils.HttpInterceptor.EntityRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
//    @Value("${spring.application.files.logs.user}")
//    private String userLogs;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private Clientinformation clientinformation;
    @Autowired
    private UserRepository usersRepository;
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            log.info("-------------------------------Authentication Entry");
            Enumeration<String> headerNames = request.getHeaderNames();
//            if ("POST".equalsIgnoreCase(request.getMethod()))
//            {
//                String test = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
//                System.out.println("Body: "+test);
////            }
//            if (headerNames != null) {
//                while (headerNames.hasMoreElements()) {
//                    String name = headerNames.nextElement();
//                    System.out.println(name+": " + request.getHeader(name));
//                }
//            }
            String accessToken = request.getHeader("Authorization");

//            log.info("{} {}", request.getRequestURI(), request.getMethod());
            if (!Objects.isNull(accessToken)) {
                accessToken = accessToken.replaceAll("Bearer ", "");
            }

            EntityRequestContext.setCurrentEntityId(request.getHeader("entityId"));

            if (request.getRequestURI().matches("/auth/signin") || request.getRequestURI().matches("/auth/signup") || request.getRequestURI().matches("/swagger-ui/")) {
                UserRequestContext.setCurrentUserName("Guest");
                EntityRequestContext.setCurrentEntityId("001");
            }

            String jwt = accessToken;
            clientinformation.getClientInformation(request);
            System.out.println("jwt: "+jwt);

            if (jwt != null && !jwt.equals("jcbnvdsgcvsdggvgvcvsdhghdsdwodweidwebdfhbvweh326432fdwbhgcdf4736bvcghf36vgvdgy4r76t37t")) {
            if (jwtUtils.validateJwtToken(jwt)) {
                System.out.println("Jwt is not null");
                String email = jwtUtils.getUserNameFromJwtToken(jwt);
                System.out.println("email");
                Map<String, Object> jwtHeaders = jwtUtils.getHeadersFromJwtToken(jwt);
                Optional<User> user = usersRepository.findByEmail(email);

                UserRequestContext.setCurrentUserName(email);
                UserRequestContext.setCurrentUser(user.get());

                EntityRequestContext.setCurrentEntityId(String.valueOf(user.get().getId()));

                UserData userData = userService.getUserDetails(email).getUser();
                UserDetails userDetails = dataToUserDetails(userData);
                System.out.println("userDetails"+userDetails);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

//                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Authenticated");
            }else {
                System.out.println("Could not authenticate jwt");
            }
            }else {
                System.out.println("JWT is null");
            }
        } catch (Exception e) {
//            e.printStackTrace();
            JwtStatusContext.setExpiredJWT(true);
            log.info("Could not be authenticated");
            SecurityContextHolder.clearContext();
        } finally {
            try {
                getLogs(request);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
//        System.out.println("Before filterchain");
        filterChain.doFilter(request, response);
//        System.out.println("After filterchain");
    }

    private UserDetails dataToUserDetails(UserData userData) {
        UserDetails usd = new UserDetails() {
            String otp;

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<GrantedAuthority> authorities = new ArrayList<>();

                userData.getRoles().forEach(userRoleData -> {
                    authorities.addAll(userRoleData.getAccessRights().stream().map(roleAccessRights -> new SimpleGrantedAuthority(roleAccessRights.getAccessRights().name())).collect(Collectors.toSet()));
                });
                return authorities;
            }

            @Override
            public String getPassword() {
                return userData.getPassword();
            }

            @Override
            public String getUsername() {
                return userData.getEmail();
            }

            @Override
            public boolean isAccountNonExpired() {
                return false;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        };

        return usd;
    }

    public void getLogs(HttpServletRequest request) {
//        String currentUserDetails = null;
//        try {
//            System.out.println("Context: "+SecurityContextHolder.getContext());
//            System.out.println("Authentication: "+SecurityContextHolder.getContext().getAuthentication());
//            System.out.println("Principal"+SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//            currentUserDetails =
//                    ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info("Error is: {}", e.getMessage());
//        }
//
//        if (currentUserDetails == null || currentUserDetails.trim().isEmpty()) {
//            currentUserDetails = "guest";
//        }
////        log.info("Remote user is: {}", request.getRemoteHost());
////        log.info("-------------------------------Saved " + currentUserDetails + " request logs");
//        String method = request.getMethod();
//        String uri = request.getRequestURI();
//        String queryString = request.getQueryString();
//        String protocol = request.getProtocol();
//        String remoteAddr = request.getRemoteAddr();
//        int remotePort = request.getRemotePort();
//        String userAgent = request.getHeader("User-Agent");
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        String fileName = currentUserDetails.toLowerCase() + "_" + now.format(formatter) + "_requests.log";
    }

}