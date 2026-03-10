package ke.co.myfuture.Myfuture;

import ke.co.myfuture.Myfuture.Commonauth.Auth.RoleConfig.AccessRight;
import ke.co.myfuture.Myfuture.Commonauth.Auth.RoleConfig.RoleConfig;
import ke.co.myfuture.Myfuture.Commonauth.Auth.RoleConfig.RoleConfigRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserService;
import ke.co.myfuture.Myfuture.Commonauth.Auth.UserPasswords.UserPassword;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.PasswordUtil;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.SendCredentialsToEmail;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Role.ERole;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@Log
@EnableEncryptableProperties
public class SecureApplication {
    @Value("${organisation.superUserEmail}")
    private String superUserEmail;
    @Value("${organisation.superUserFirstName}")
    private String superUserFirstName;
    @Value("${organisation.superUserLastName}")
    private String superUserLastName;

    @Value("${organisation.enableMaker}")
    private String enableMaker;
    @Value("${production}")
    private boolean inProd;
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private final UserService userService;

    @Autowired
    private final RoleConfigRepository roleConfigRepository;
//    @Autowired
//    private SchoolService schoolService;

    public SecureApplication(UserRepository userRepository, UserService userService, RoleConfigRepository roleConfigRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.roleConfigRepository = roleConfigRepository;
    }

    public static void main(String[] args) throws IOException {
        SpringApplication.run(SecureApplication.class, args);
    }



    private void initRoles() {
        List<RoleConfig> currentRoles = roleConfigRepository.findAll();
        System.out.println("Printing roles");
        System.out.println(Arrays.deepToString(currentRoles.toArray()));
        System.out.println("After Printing roles");
        List<AccessRight> accessRights = Arrays.asList(AccessRight.values());
        log.log(Level.INFO, String.format("Access Rights %s : ", accessRights));

        if (roleConfigRepository.findByName(ERole.ROLE_SUPERUSER.toString()).isEmpty()) {
            AtomicReference<RoleConfig> roleConfig = new AtomicReference<>(new RoleConfig());
            roleConfig.get().setName(ERole.ROLE_SUPERUSER.toString());
            roleConfig.get().setAccessRights(accessRights);
            roleConfig.get().setStatus(1);
            roleConfig.set(roleConfigRepository.save(roleConfig.get()));
        }
    }

    private void updateRole() {
        List<AccessRight> accessRights = Arrays.asList(AccessRight.values());
        log.log(Level.INFO, String.format("Access Rights %s : ", accessRights));
        Optional<RoleConfig> roleConfig = roleConfigRepository.findByName(ERole.ROLE_SUPERUSER.toString());
        if (roleConfig.isPresent()) {
            roleConfig.get().setAccessRights(accessRights);
            roleConfigRepository.save(roleConfig.get());
        }
    }

    private void initMaker() {
        if (userRepository.findAll().isEmpty()) {
            initRoles();
            AtomicReference<User> user = new AtomicReference<>(new User());
            roleConfigRepository.findByName(ERole.ROLE_SUPERUSER.toString()).ifPresentOrElse(roleConfig -> {
                    user.get().setEmail(superUserEmail.trim());
                    user.get().setFirstName(superUserFirstName.trim());
                    user.get().setLastName(superUserLastName.trim());
                    user.get().setStatus("Active");
                    user.get().setFirstLogin(1);
                    UserPassword userPassword = new UserPassword();
                    userPassword.setPassword(passwordUtil.encode("12345678"));
                    user.get().setPasswords(List.of(userPassword));
                    user.set(userRepository.save(user.get()));
                    log.log(Level.INFO, String.format("User created [ %s ]", user.get()));
                    log.log(Level.INFO, String.format("Role Details [ %s ]", roleConfig));

                    if (this.userService.assignRole(user.get(), roleConfig, true)) {
                        log.log(Level.INFO, String.format("User assigned role [ %s ]", user.get()));
                    }
                    try {
                        SendCredentialsToEmail sm = new SendCredentialsToEmail();
                        log.log(Level.INFO, String.format("User Email [ %s ]", user.get().getEmail()));
                        if (inProd)
                            sm.sendMail(user.get().getEmail(), user.get().getFirstName() + " " + user.get().getLastName(), "12345678");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, () -> log.log(Level.WARNING, "Provided role is not found")
            );
        }
    }
//    public void loadSchools() throws IOException {
//        schoolService.loadSchoolExcelFiles();
//    }



    @Bean
    CommandLineRunner runner() {
        return args -> {
            if (enableMaker.equalsIgnoreCase("true")) {
                initMaker();
                updateRole();
            }
            System.out.println("Initialization is okay");
        };
    }

}