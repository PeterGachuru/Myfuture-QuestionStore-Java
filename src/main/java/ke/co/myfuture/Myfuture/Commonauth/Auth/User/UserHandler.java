package ke.co.myfuture.Myfuture.Commonauth.Auth.User;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Request.User.UpdateUserRequest;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Request.User.UpdateUserRoleRequest;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Request.User.UserCreateRequest;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.AuthEntityResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.User.UserResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.User.UsersResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.Response.OtpResponse;
import ke.co.myfuture.Myfuture.Commonauth.CustomerExceptions.MailServiceException;
import ke.co.myfuture.Myfuture.Commonauth.CustomerExceptions.MakerCheckerFailException;
import ke.co.myfuture.Myfuture.Commonauth.Utils.CustomMailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("users")
public class UserHandler {
    @Autowired
    UserService userService;
    @Autowired
    private UserRepository userRepository;


    @PostMapping("/register")
    public ResponseEntity<AuthEntityResponse> createRegister(@RequestBody UserCreateRequest body) {
        AuthEntityResponse response = new AuthEntityResponse();
        if (!body.getEmail().contains("@")) return null;

        response = this.userService.createUser(body);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/create-user")
    public ResponseEntity<AuthEntityResponse> createUser(@RequestBody UserCreateRequest body) {
        AuthEntityResponse response = new AuthEntityResponse();
        if (!body.getEmail().contains("@")) return null;

        response = this.userService.createUser(body);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/accept-terms")
    public ResponseEntity<OtpResponse> acceptTerms() {
        System.out.println("Here");
        try {
            userService.acceptTerms();
            return ResponseEntity.ok(new OtpResponse(null, "success"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new OtpResponse(null,
                    ex.getMessage()));
        }
    }

    @PutMapping("/update-user")
    public ResponseEntity<AuthEntityResponse> updateUser(@RequestBody UpdateUserRequest body) {
        AuthEntityResponse response = this.userService.updateUser(body);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/update-user-role")
    public ResponseEntity<?> updateUserRole(@RequestBody UpdateUserRoleRequest body) {
        try {
            AuthEntityResponse response = this.userService.updateUserRole(body.getEmail(), body.getRoleId());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new OtpResponse(null, e.getMessage()));
        }
    }

    @PutMapping("/lock-user/{email}")
    public ResponseEntity<AuthEntityResponse> lockUser(@PathVariable String email) {
//        AuthEntityResponse response = this.userService.updateUserStatus(email, "Locked");

        return ResponseEntity.ok().body(null);
    }

    @PutMapping("/unlock-user-account/{email}")
    public ResponseEntity<AuthEntityResponse> unlockUser(@PathVariable String email) {
        AuthEntityResponse response = this.userService.updateUserStatus(email, "Active");

        return ResponseEntity.ok().body(response);
    }


    @PutMapping("/delete-user/{email}")
    public ResponseEntity<AuthEntityResponse> deleteUser(@PathVariable String email) {
        AuthEntityResponse response = this.userService.updateUserStatus(email, "Deleted");

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/restore-user/{email}")
    public ResponseEntity<AuthEntityResponse> restoreUser(@PathVariable String email) {
        AuthEntityResponse response = this.userService.updateUserStatus(email, "Active");

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/all-accounts")
    public ResponseEntity<UsersResponse> getAllAccounts() {
        UsersResponse users = this.userService.getAllUsers();

        if (users != null) {
            return ResponseEntity.ok().body(users);
        } else {
            return ResponseEntity.ok().body(new UsersResponse());
        }
    }

    @GetMapping("byemail")
    public ResponseEntity<UserResponse> getUserDetails(@RequestParam("email") String email) {
        UserResponse user = this.userService.getUserDetails(email);

        if (user != null) {
            return ResponseEntity.ok().body(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/active-accounts")
    public ResponseEntity<UsersResponse> fetchAllActiveUserAccounts() {
        UsersResponse users = this.userService.getUsersByStatus("Active");

        if (users != null) {
            return ResponseEntity.ok().body(users);
        } else {
            return ResponseEntity.ok().body(new UsersResponse());
        }
    }

    @GetMapping("/locked-accounts")
    public ResponseEntity<UsersResponse> fetchAllLockedUserAccounts() {
        UsersResponse users = this.userService.getUsersByStatus("Locked");

        if (users != null) {
            return ResponseEntity.ok().body(users);
        } else {
            return ResponseEntity.ok().body(new UsersResponse());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody String email) {
        try {
            if (this.userService.adminResetPassword(email)) {
                return ResponseEntity.ok().body(new AuthEntityResponse(200, "User password reset successfully"));
            } else {
                return ResponseEntity.ok().body(new AuthEntityResponse(400, "User password failed to rest."));
            }
        } catch (MakerCheckerFailException ignored) {
            return ResponseEntity.status(403).body(new OtpResponse(null, "You cannot reset your own account password"));
        } catch (MailServiceException e) {
            return ResponseEntity.status(403).body(new OtpResponse(null, e.getMessage()));
        }

    }

    @PostMapping("/admin-kick-out")
    public ResponseEntity<?> adminKickOut(@RequestBody String email) {
        try {
            this.userService.adminKickOut(email);
            return ResponseEntity.status(200).body(new OtpResponse(null, "User kicked out"));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(new OtpResponse(null, e.getMessage()));
        }
    }

    @GetMapping("/deleted-accounts")
    public ResponseEntity<UsersResponse> fetchAllDeletedUserAccounts() {
        UsersResponse users = this.userService.getUsersByStatus("Deleted");

        if (users != null) {
            return ResponseEntity.ok().body(users);
        } else {
            return ResponseEntity.ok().body(new UsersResponse());
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics() {
        Object analytics = userRepository.getAnalytics();
        return ResponseEntity.ok().body(analytics);
    }
}
