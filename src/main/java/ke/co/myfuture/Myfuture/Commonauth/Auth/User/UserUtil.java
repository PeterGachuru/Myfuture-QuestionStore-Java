package ke.co.myfuture.Myfuture.Commonauth.Auth.User;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.*;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Otp.OtpService;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.JwtUtils;
import ke.co.myfuture.Myfuture.Commonauth.Auth.UserRole.UserRole;
import ke.co.myfuture.Myfuture.Commonauth.Auth.UserRole.UserRoleRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
@Slf4j
public class UserUtil {
    private final JwtUtils jwtUtils;
    private final OtpService otpService;
    private final LoginSessionRepository loginSessionRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public LoginSession loginBuilder(UserData userData) {
        System.out.println("About to generate jwt token");
        String token = jwtUtils.generateJwtToken(userData, true);
        String refreshToken = jwtUtils.generateJwtToken(userData, true);
        String otp = this.otpService.generateOTP(userData.getEmail(), token);
        log.info("Otp is: {}", otp);
        LoginSession authResponse =  LoginSession.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userId(userData.getId())
                .firstName(userData.getFirstName())
                .phoneNumber(userData.getPhoneNumber())
                .lastName(userData.getLastName())
                .email(userData.getEmail())
                .firstLogin(userData.getFirstLogin())
                .hasAcceptedTerms(userData.getHasAcceptedTerms())
                .roles(userData.getRoles())
                .build();

        return  loginSessionRepository.save(authResponse);
    }

    public UserResponse getUserDetails(@NonNull String email) {
        AtomicReference<UserResponse> response = new AtomicReference<>();

        this.userRepository.findByEmail(email).ifPresentOrElse(user -> {
            response.set(UserResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("User Details Found")
                    .user(getUserDetails(user)).build());
        }, () -> {

        });

        return response.get();
    }

    public UserData getUserDetails(User user) {
        AtomicReference<UserResponse> response = new AtomicReference<>();

        UserData data = UserData.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .county(user.getCounty())
                .pictureUrl(user.getPictureUrl())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .creationDate(user.getCreationDate())
                .updateDate(user.getUpdateDate())
                .isLoggedIn(user.getIsLoggedIn())
                .hasAcceptedTerms(user.getHasAcceptedTerms())
                .firstLogin(user.getFirstLogin())
                .build();

        List<UserRoleData> roles = new ArrayList<>();

        List<UserRole> userRoles = this.userRoleRepository.findAllByUser(user);

        if (userRoles != null && !userRoles.isEmpty()) {
            userRoles.forEach(userRole -> {
                UserRoleData userRoleData = UserRoleData.builder()
                        .name(userRole.getRole().getName())
                        .build();

                List<RoleAccessRights> accessRights = new ArrayList<>();
                if (userRole.getRole().getStatus() != null && !userRole.getRole().getAccessRights().isEmpty()) {
                    userRole.getRole().getAccessRights().forEach(accessRight -> {
                        accessRights.add(RoleAccessRights.builder().name(accessRight.getName()).accessRights(accessRight).build());
                    });
                }

                userRoleData.setAccessRights(accessRights);

                roles.add(userRoleData);

            });
            data.setRoles(roles);
        }

        return data;
    }
}
