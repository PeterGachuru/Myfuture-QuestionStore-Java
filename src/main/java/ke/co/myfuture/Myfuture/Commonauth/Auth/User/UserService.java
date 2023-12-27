package ke.co.myfuture.Myfuture.Commonauth.Auth.User;

//import co.ke.emtechhousee.emtr.Auditing.AuditTrail.AuditTrailProvider;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Request.User.UpdateUserRequest;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.AuthEntityResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.User.LoginResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.User.UserResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.User.UsersResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Role.RoleAccessRights;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.User.LoginData;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.User.UserData;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.User.UserRoleData;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Otp.OtpService;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Role.RoleConfig;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Role.RoleConfigRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.UserPasswords.UserPassword;
import ke.co.myfuture.Myfuture.Commonauth.Auth.UserRole.UserRole;
import ke.co.myfuture.Myfuture.Commonauth.Auth.UserRole.UserRoleRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.PasswordGenerator;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.PasswordUtil;
import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.JwtUtils;
import ke.co.myfuture.Myfuture.Commonauth.CustomerExceptions.MailServiceException;
import ke.co.myfuture.Myfuture.Commonauth.CustomerExceptions.MakerCheckerFailException;
import ke.co.myfuture.Myfuture.Commonauth.CustomerExceptions.MaximumRetriesException;
import ke.co.myfuture.Myfuture.Commonauth.MailComponent.MailService2;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final RoleConfigRepository roleConfigRepository;

    private final UserRoleRepository userRoleRepository;

    private final PasswordUtil passwordUtil;

    private final JwtUtils jwtUtils;
    private final PasswordGenerator passwordGenerator = new PasswordGenerator();
    private final OtpService otpService;

    private final MailService2 mailService2;
    @Value("${production}")
    private boolean inProd;

    public List<RoleConfig> validateUser(@NonNull String email) {
        List<RoleConfig> roles = new ArrayList<>();

        this.userRepository.findByEmail(email.trim()).ifPresent(user -> {
            if (Objects.equals(user.getStatus(), "Active")) {
                roles.addAll(this.userRoles(user, true));
            }
        });

        return roles;
    }

    public Optional<User> getLoggedInUser(String email) {
        return this.userRepository.findByEmail(email);
    }

    public List<RoleConfig> userRoles(@org.springframework.lang.NonNull User user, boolean activeOnly) {
        if (activeOnly) {
            return this.userRoleRepository.findAllByUser(user).stream().map(UserRole::getRole).collect(Collectors.toList());
        } else {
            return this.userRoleRepository.findAllByUserAndStatus(user, 1).stream().map(UserRole::getRole).collect(Collectors.toList());
        }
    }

    public LoginResponse authenticateUser(@NonNull String email, @NonNull String password) {
        System.out.println("In authenticateUser");
        AtomicReference<LoginResponse> response = new AtomicReference<>();

        userRepository.findByEmail(email.trim()).ifPresentOrElse(user -> {
            System.out.println("Found user "+email);
            if (Objects.equals(user.getStatus(), "Active")) {
                System.out.println("User is active");
                if (!otpService.validateLoginRetries(email)) {
                    System.out.println("User is locked");
                    updateUserStatus(email, "Locked");
                    response.set(LoginResponse.builder().status(HttpStatus.FORBIDDEN.value()).message("Maximum login " +
                            "retries have been reached. Your account has been locked. Contact your system adimin").build());
                    return;
                }

                System.out.println("User otps have been validated");

                Calendar lastLogin = Calendar.getInstance();
                lastLogin.setTime(user.getLastLogin());
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());

                lastLogin.add(Calendar.MINUTE, 10);

                System.out.println("Before check of number of logins");

                if (inProd && user.getIsLoggedIn() == 1 && lastLogin.after(now)) {
                    System.out.println("More than one login");
                    response.set(LoginResponse.builder().status(HttpStatus.FORBIDDEN.value()).message("User cannot " +
                            "login to have more than one session").build());
//                    audit.log("AUTHENTICATION", "Attempted multiple session login for user: ", email);
                    System.out.println("Returning");
                    return;
                }

                System.out.println(" before Check password match");

                if (passwordUtil.matches(password.trim(),
                        user.getPasswords().get(user.getPasswords().size() - 1).getPassword())) {
                    System.out.println("Password does match");

                    UserData userData = getUserDetails(user.getEmail()).getUser();

                    if (user.getEmail().equalsIgnoreCase("no-reply@equitybank.co.ke") && user.getFirstLogin() == 0) {
                        response.set(LoginResponse.builder().status(HttpStatus.UNAUTHORIZED.value()).message("Account" +
                                " Is Deactivated").build());
                        return;
                    }
                    System.out.println("About to generate jwt token");
                    String token = jwtUtils.generateJwtToken(userData, true);
                    String otp = this.otpService.generateOTP(userData.getEmail(), token);
                    log.info("Otp is: {}", otp);
                    LoginData authResponse = LoginData.builder()
                            .token(token)
                            .id(userData.getId())
                            .firstName(userData.getFirstName())
                            .lastName(userData.getLastName())
                            .email(userData.getEmail())
                            .firstLogin(userData.getFirstLogin())
                            .hasAcceptedTerms(userData.getHasAcceptedTerms())
                            .roles(userData.getRoles())
                            .build();
                    otpService.resetAllRetries(email);
                    try {
                        log.info("otp is {}",otp);
                        System.out.println("About to send email");
                        if (inProd)
                            mailService2.sendEmail(userData.getEmail(), "Your OTP is: " + otp, "OTP");



                        response.set(LoginResponse.builder().status(HttpStatus.OK.value()).message("Login successful").user(authResponse).build());

                    } catch (MailServiceException e) {
                        e.printStackTrace();
                        response.set(LoginResponse.builder().status(HttpStatus.SERVICE_UNAVAILABLE.value()).message(e.getMessage()).build());
                    }
                } else {
                    //to-do lock user if password is invalid 5 times
                    System.out.println("Password does not match");
                    response.set(LoginResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message("Error").build());
                }
            } else {
                response.set(LoginResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message("Account is " +
                        "inactive. Please contact your system admin.").build());
            }
        }, () -> {
            System.out.println("Did not find user");
            response.set(LoginResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message("Error").build());
        });

        return response.get();
    }

    public AuthEntityResponse createUser(@NonNull String email, @NonNull String firstName, @NonNull String lastName,
                                         @NonNull Long role) {
        System.out.println("Creating user");
        AtomicReference<AuthEntityResponse> res = new AtomicReference<>();

        userRepository.findByEmail(email).ifPresentOrElse(myUser -> {
            res.set(AuthEntityResponse.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message(String.format(
                    "User with the email %s already exists ", email)).build());
        }, () -> this.roleConfigRepository.findById(role).ifPresentOrElse(roleConfig -> {
            if (roleConfig.getStatus().compareTo(1) == 0) {
                User user = new User();
                user.setEmail(email.trim());
                user.setFirstName(firstName.trim());
                user.setLastName(lastName.trim());
                user.setStatus("Active");
                user.setFirstLogin(1);


                String password = passwordGenerator.generatePassword();
                System.out.println(password);
                UserPassword userPassword = new UserPassword();
                userPassword.setPassword(passwordUtil.encode(password));
                List<UserPassword> userPasswords = new ArrayList<>();
                userPasswords.add(userPassword);

                user.setPasswords(userPasswords);

                try {
                    if (inProd)
                        mailService2.sendEmail(user.getEmail(),
                                "Your Myfuture password is: " + password + "  Do not share your password with anyone",
                                "Myfuture password");

                    log.info("Password: {}", password);
                    if (inProd) {
                        mailService2.sendEmail(user.getEmail(),
                                "Your Myfuture password is: " + password + "  Do not share your password with anyone",
                                "Myfuture password");
                        log.info("password {}",password);
                    };


                    user = userRepository.save(user);

                    UserRole usr = new UserRole();
                    usr.setRole(roleConfig);
                    usr.setUser(user);
                    usr.setCreation_date(new Timestamp(new Date().getTime()));
                    usr.setStatus(1);
                    usr.setUpdate_date(new Timestamp(new Date().getTime()));

                    userRoleRepository.save(usr);
//                    audit.log("USER ACCOUNTS", "Creating user:", user.getEmail(), "with role", roleConfig.getName());
                    res.set(AuthEntityResponse.builder().message("User created successfully").statusCode(HttpStatus.OK.value()).build());
                } catch (MailServiceException e) {
                    e.printStackTrace();
                    res.set(AuthEntityResponse.builder().message(e.getMessage()).statusCode(HttpStatus.BAD_REQUEST.value()).build());
                }
            } else {
                res.set(AuthEntityResponse.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("Provided " +
                        "role is not active").build());
            }
        }, () -> res.set(AuthEntityResponse.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("Provided " +
                "role is not found").build())));

        return res.get();
    }

    public boolean assignRole(@NonNull User user, @NonNull RoleConfig role, boolean activate) {
        AtomicBoolean res = new AtomicBoolean();

        this.userRepository.findById(user.getId()).ifPresentOrElse(userData -> {
            if (Objects.equals(userData.getStatus(), "Active")) {
                this.roleConfigRepository.findById(role.getId()).ifPresentOrElse(myRole -> {
                    if (myRole.getStatus().compareTo(1) == 0) {
                        userRoleRepository.findByUserAndRole(userData, myRole).ifPresentOrElse(ur -> {
                            AtomicReference<UserRole> userRole = new AtomicReference<>(ur);
                            if (activate) {
                                userRole.get().setStatus(1); /* 1 - activate */
                            } else {
                                userRole.get().setStatus(2); /* 2 - disabled */
                            }

                            userRole.set(this.userRoleRepository.save(userRole.get()));

                            res.set(true);
                        }, () -> {
                            if (activate) {
                                AtomicReference<UserRole> userRole = new AtomicReference<>(new UserRole());
                                userRole.get().setUser(userData);
                                userRole.get().setRole(myRole);
                                userRole.get().setStatus(1);
                                userRole.set(this.userRoleRepository.save(userRole.get()));
                                res.set(true);
                            }
                        });
                    } else {
                        res.set(false);
                    }
                }, () -> {
                    res.set(false);
                });
            } else {
                res.set(false);
            }
        }, () -> {


            res.set(false);
        });

        return res.get();
    }


    public AuthEntityResponse updateUser(UpdateUserRequest body) {
        AtomicReference<AuthEntityResponse> res = new AtomicReference<>();

        userRepository.findById(body.getId()).ifPresentOrElse(myUser -> {
            AtomicReference<User> user = new AtomicReference<>(myUser);
            user.get().setFirstName(body.getFirstName().trim());
            user.get().setLastName(body.getLastName().trim());

            user.set(this.userRepository.save(user.get()));

//            audit.log("USER ACCOUNTS", "Updating user details for user", user.get().getEmail());
            res.set(AuthEntityResponse.builder().message("User details updated successfully !").statusCode(HttpStatus.OK.value()).build());
        }, () -> {
            res.set(AuthEntityResponse.builder().message("User not found !").statusCode(HttpStatus.BAD_REQUEST.value()).build());
        });

        return res.get();
    }

    public AuthEntityResponse updateUserStatus(@NonNull String email, @NonNull String status) {
        AtomicReference<AuthEntityResponse> response = new AtomicReference<>();

        this.userRepository.findByEmail(email).ifPresentOrElse(userData -> {
            AtomicReference<User> user = new AtomicReference<>(userData);
            user.get().setStatus(status);

            user.set(this.userRepository.save(user.get()));

            switch (status) {
                case "Active" -> {
//                    audit.log("USER ACCOUNTS", "Activating user:", user.get().getEmail());
                    otpService.resetAllRetries(email);
                }
//                case "Locked" -> audit.log("USER ACCOUNTS", "Locking user:", user.get().getEmail());
//                case "Deleted" -> audit.log("USER ACCOUNTS", "Deleting user:", user.get().getEmail());
            }

            response.set(AuthEntityResponse.builder().message("User status updated successfully !").statusCode(HttpStatus.OK.value()).build());
        }, () -> {
            /* todo:: User not found  */
            response.set(AuthEntityResponse.builder().message("User not found").statusCode(HttpStatus.BAD_REQUEST.value()).build());
        });

        return response.get();
    }

    public AuthEntityResponse updateUserPassword(@NonNull String email, @NonNull String previousPassword,
                                                 @NonNull String password) {
        AtomicReference<AuthEntityResponse> response = new AtomicReference<>();

        this.userRepository.findByEmail(email).ifPresentOrElse(userData -> {
            if (Objects.equals(userData.getStatus(), "Active")) {

                if (passwordUtil.matches(previousPassword.trim(),
                        userData.getPasswords().get(userData.getPasswords().size() - 1).getPassword())) {

                    List<UserPassword> passwords = userData.getPasswords();
                    String encodedPassword = passwordUtil.encode(password);

                    boolean passwordExists =
                            passwords.stream().anyMatch(userPassword -> userPassword.getPassword().equals(encodedPassword));

                    if (passwordExists) {
                        response.set(AuthEntityResponse.builder().message("New password cannot equal old password!").statusCode(HttpStatus.FORBIDDEN.value()).build());
                        return;
                    } else {
                        UserPassword userPassword = new UserPassword();
                        userPassword.setPassword(encodedPassword);

                        if (passwords.size() == 12) {
                            passwords.remove(0);
                        }

                        passwords.add(userPassword);
                        userData.setPasswords(passwords);
                    }

                    userData.setFirstLogin(0);
                    userRepository.save(userData);

                    response.set(AuthEntityResponse.builder().message("Password updated successfully !").statusCode(HttpStatus.OK.value()).build());
//                    audit.log("USERS ACCOUNT", "Updating own password");
                } else {
                    response.set(AuthEntityResponse.builder().message("The previous  password you provided is " +
                            "incorrect !").statusCode(HttpStatus.BAD_REQUEST.value()).build());
                }

            } else {


                response.set(AuthEntityResponse.builder().message(String.format("Account with the email %s is not " +
                        "active ", email)).statusCode(HttpStatus.BAD_REQUEST.value()).build());

            }
        }, () -> {
            /* todo:: User not found  */
            response.set(AuthEntityResponse.builder().message(String.format("Account with the email %s not found ",
                    email)).statusCode(HttpStatus.BAD_REQUEST.value()).build());
        });

        return response.get();
    }


    public AuthEntityResponse updateUserRole(@NonNull String email, @NonNull Long roleId) throws RuntimeException {
        AuthEntityResponse response = new AuthEntityResponse();
        String adminEmail =
                ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        roleConfigRepository.findById(roleId).ifPresentOrElse((roleConfig -> {
            if (roleConfig.getStatus() == 0) {
                response.setMessage("You cannot assign a locked role");
                return;
            }
            userRoleRepository.findByUser_Email(email).ifPresentOrElse((userRole -> {
                System.out.println("Previous role was " + userRole.getRole().getName() + " Current is " + roleConfig.getName());
                String oldRole = userRole.getRole().getName();
                userRoleRepository.updateUserRole(roleId, userRole.getUser().getId());

                if (userRole.getUser().getEmail().trim().toLowerCase().equals(adminEmail.toLowerCase().trim())) {
//                    audit.log("USER-ACCOUNT", "Attempting to update own role");
                    throw new RuntimeException("You cannot re-assign yourself a role");
                }

                try {
                    String newRole = userRoleRepository.findById(roleId).get().getRole().getName();

                    User user = userRepository.findByEmail(email).get();

//                    audit.log("USER ACCOUNTS", "Updating user role for user:", user.getEmail(), "From:", oldRole, "To" +
//                            ":", newRole);
                } catch (Exception ignored) {
                } finally {
                    response.setMessage("User role updated successfully");

                    response.setStatusCode(201);
                }

            }), () -> {
                response.setMessage("User not present");
                response.setStatusCode(404);
            });
        }), () -> {
            response.setMessage("Role not found");
            response.setStatusCode(404);
        });

        return response;
    }


    public UsersResponse getAllUsers() {
        AtomicReference<UsersResponse> response = new AtomicReference<>();

        java.util.List<UserData> usersResponse = new ArrayList<>();
        java.util.List<User> users = this.userRepository.findAll();

        if (!users.isEmpty()) {
            users.forEach(user -> {
                UserData userData = UserData.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .status(user.getStatus())
                        .creationDate(user.getCreationDate())
                        .updateDate(user.getUpdateDate())
                        .isLoggedIn(user.getIsLoggedIn())
                        .build();

                java.util.List<UserRoleData> roles = new ArrayList<>();

                java.util.List<UserRole> userRoles = this.userRoleRepository.findAllByUser(user);
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
                        userData.setRoles(roles);
                    });
                }

                usersResponse.add(userData);
            });

            response.set(UsersResponse.builder().status(HttpStatus.OK.value()).message("Users Found").users(usersResponse).build());
        }

        return response.get();
    }

    public UserResponse getUserDetails(@NonNull String email) {
        AtomicReference<UserResponse> response = new AtomicReference<>();

        this.userRepository.findByEmail(email).ifPresentOrElse(user -> {
            UserData data = UserData.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
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

            response.set(UserResponse.builder().status(HttpStatus.OK.value()).message("User Details Found").user(data).build());
        }, () -> {

        });

        return response.get();
    }

    public UsersResponse getUsersByStatus(@org.springframework.lang.NonNull String status) {
        AtomicReference<UsersResponse> response = new AtomicReference<>();

        List<UserData> usersResponse = new ArrayList<>();
        List<User> users = this.userRepository.findByStatus(status);

        if (users != null && !users.isEmpty()) {
            users.forEach(user -> {
                UserData userData = UserData.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .status(user.getStatus())
                        .creationDate(user.getCreationDate())
                        .updateDate(user.getUpdateDate())
                        .isLoggedIn(user.getIsLoggedIn())
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

                        userData.setRoles(roles);
                    });
                }

                usersResponse.add(userData);
            });

            response.set(UsersResponse.builder().status(HttpStatus.OK.value()).message("Users Found").users(usersResponse).build());
        }

        return response.get();
    }

    public Boolean adminResetPassword(String email) throws MakerCheckerFailException, MailServiceException {
        String adminEmail =
                ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        if (adminEmail.equalsIgnoreCase(email.trim())) {
            throw new MakerCheckerFailException();
        }

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            String password = passwordGenerator.generatePassword();
            String encodedPassword = passwordUtil.encode(password);

            List<UserPassword> passwords = user.getPasswords();
            UserPassword userPassword = new UserPassword();
            userPassword.setPassword(encodedPassword);
            user.setFirstLogin(1);

            if (passwords.size() == 12) {
                passwords.remove(0);
            }
            passwords.add(userPassword);
            user.setPasswords(passwords);

//            audit.log("USERS", "Resetting password for user: ", user.getEmail());
            if (inProd) {
                mailService2.sendEmail(email, "Your email have been reset successfully: Use the following password to to " +
                        "login: " + password, "Password Reset");

            }
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public String otp(String otp) throws MaximumRetriesException {
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserData userData = getUserDetails(userDetails.getUsername()).getUser();
        Boolean tokenIsValid = this.otpService.validateOtp(userDetails.getUsername(), otp);

        if (tokenIsValid) {
//            audit.log("AUTHENTICATION", "Login in");
            String jwt = jwtUtils.generateJwtToken(userData);
            otpService.saveToken(userData.getEmail(), jwt);

            //update last login
            User user = userRepository.findByEmail(userDetails.getUsername()).get();

            if (!user.getHasAcceptedTerms()) {
                System.out.println("Here");
                throw new RuntimeException("Kindly accept terms first");
            }

            user.setLastLogin(new Timestamp(System.currentTimeMillis()));
            user.setIsLoggedIn(1);
            userRepository.save(user);

            return jwt;
        } else {
//            audit.log("AUTHENTICATION", "Attempting to input an invalid OTP");
            return null;
        }

    }

    public void adminKickOut(String email) throws RuntimeException {
        Optional<User> userOpt = this.userRepository.findByEmail(email);
        String adminEmail =
                ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getEmail().trim().toLowerCase().equals(adminEmail.toLowerCase().trim())) {
                throw new RuntimeException("You cannot kick yourself out of the system");
            }

            if (user.getIsLoggedIn() == 0) {
                throw new RuntimeException("User is not logged in");
            } else {
                user.setIsLoggedIn(0);
                otpService.invalidateAllOtps(email);
                otpService.deletePreviousTokens(email);
                userRepository.save(user);
//                audit.log("USER-ACCOUNT", "Kicking user:", user.getEmail(), "out of the system");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public String resendOtp() throws MailServiceException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserData userData = getUserDetails(userDetails.getUsername()).getUser();

        String jwt = jwtUtils.generateJwtToken(userData);
        String otp = otpService.generateOTP(userData.getEmail(), jwt);
        log.info("Your OTP is: {}", otp);
//        audit.log("AUTHENTICATION", "Requesting a new OTP");
        if (inProd) {
           this.mailService2.sendEmail(userDetails.getUsername(), "Your OTP is: " + otp, "Recon Master OTP");
        }
        return jwt;

    }

    public void logOut() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        audit.log("AUTHENTICATION", "Login out");

        Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());

        if (userOpt.isPresent()) {
            System.out.println("Is present");
            User user = userOpt.get();
            user.setIsLoggedIn(0);
            userRepository.save(user);
        }

        System.out.println("Not present");
        this.otpService.deletePreviousTokens(userDetails.getUsername());

    }

    public void acceptTerms() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = userRepository.findByEmail(userDetails.getUsername());

        if (user.isPresent()) {
            user.get().setHasAcceptedTerms(true);
            userRepository.save(user.get());
        } else {
            throw new RuntimeException("User could not be found");
        }
    }
}