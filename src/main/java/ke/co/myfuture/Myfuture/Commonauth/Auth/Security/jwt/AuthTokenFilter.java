package ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.UserData;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserService;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserUtil;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.HttpInterceptor.EntityRequestContext;
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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    @Autowired
    private UserUtil userUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            log.debug("-------------------------------Authentication Entry");
            Enumeration<String> headerNames = request.getHeaderNames();
//            if ("POST".equalsIgnoreCase(request.getMethod()))
//            {
//                String test = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
//                log.debug("Body: "+test);
////            }
//            if (headerNames != null) {
//                while (headerNames.hasMoreElements()) {
//                    String name = headerNames.nextElement();
//                    log.debug(name+": " + request.getHeader(name));
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
            log.debug("jwt: "+jwt);

            if (jwt != null && !jwt.equals("jcbnvdsgcvsdggvgvcvsdhghdsdwodweidwebdfhbvweh326432fdwbhgcdf4736bvcghf36vgvdgy4r76t37t")) {
            if (jwtUtils.validateJwtToken(jwt)) {
                log.debug("Jwt is not null");
                String email = jwtUtils.getUserNameFromJwtToken(jwt);
                log.debug("email");
                Map<String, Object> jwtHeaders = jwtUtils.getHeadersFromJwtToken(jwt);
                Optional<User> user = usersRepository.findByEmail(email);

                UserRequestContext.setCurrentUserName(email);
                UserRequestContext.setCurrentUser(user.get());

                EntityRequestContext.setCurrentEntityId(String.valueOf(user.get().getId()));

                UserData userData = userUtil.getUserDetails(email).getUser();
                UserDetails userDetails = dataToUserDetails(userData);
                log.debug("userDetails: "+userDetails);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

//                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authenticated");
            }else {
                log.debug("Could not authenticate jwt");
            }
            }else {
                log.debug("JWT is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JwtStatusContext.setExpiredJWT(true);
            log.debug("Could not be authenticated");
            SecurityContextHolder.clearContext();
        } finally {
            try {
                getLogs(request);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
//        System.out.println("Before filterchain");
        System.out.println(request.getRequestURI() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
        filterChain.doFilter(request, response);
//        System.out.println("After filterchain");
    }

    private UserDetails dataToUserDetails(UserData userData) {
        UserDetails usd = new UserDetails() {
            String otp;

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<GrantedAuthority> authorities = new ArrayList<>();

                if (userData.getRoles() != null)
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
//            log.debug("Context: "+SecurityContextHolder.getContext());
//            log.debug("Authentication: "+SecurityContextHolder.getContext().getAuthentication());
//            log.debug("Principal"+SecurityContextHolder.getContext().getAuthentication().getPrincipal());
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