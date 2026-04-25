package ke.co.myfuture.Myfuture.Commonauth.Auth.User;


import ke.co.myfuture.Myfuture.Commonauth.Auth.User.PasswordReset.PasswordResetService;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.ExceptionLogger;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.LoginUserRequest;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.UpdatePasswordRequest;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.AuthEntityResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.LoginResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Otp.OtpService;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.PasswordReset.PasswordResetDTO;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.OtpRequest;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.OtpResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.JwtStatusContext;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.exception.MailServiceException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("authentication")
public class LoginController {
    @Autowired
    UserService userService;
    @Autowired
    PasswordResetService passwordResetService;
    private final OtpService otpService;
//    private final AuditTrailProvider audit;


    private final ExceptionLogger exceptionLogger;
    private final Pattern passwordPattern = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{12,60}$");

    @PostMapping("/google-signin")
    public ResponseEntity<?> googleSignIn(@RequestParam("idToken") String idTokenString,
                                          @RequestParam("installId") Long installId) {
        try {
            return ResponseEntity.ok(userService.loginByGoogle(idTokenString, installId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid ID Token");
        }
    }

    @PostMapping("/firebase-signin")
    public ResponseEntity<?> firebaseGoogleSignIn(@RequestBody GoogleSignInData googleSignInData) {
        try {
            return ResponseEntity.ok( userService.loginByGoogle(googleSignInData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid ID Token");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserRequest body) {
        System.out.println("Login request received");
        try {
            LoginResponse response = this.userService.authenticateUser(body.getEmail(), body.getPassword());
            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            System.out.println("Cannot proceed with login attempt");
            e.printStackTrace();
            System.out.println("Error");
            exceptionLogger.logError(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/loginByRefreshToken")
    public ResponseEntity<?> loginByRefreshToken(@RequestParam("token") String refreshToken) {
        System.out.println("Login request received by refreshToken");
        try {
            return ResponseEntity.ok().body(this.userService.loginByRefreshToken(refreshToken));
        }catch (Exception e) {
            System.out.println("Cannot proceed with login attempt");
            e.printStackTrace();
            System.out.println("Error");
            exceptionLogger.logError(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/expired")
    public ResponseEntity<?> expired() {
        System.out.println("check expired");
        return ResponseEntity.ok().body(JwtStatusContext.getExpiredJWT());
    }

    @PostMapping("/update-password")
    public ResponseEntity<AuthEntityResponse> updatePassword(@RequestBody UpdatePasswordRequest body) {
        AuthEntityResponse response;
        if (!Objects.isNull(body.getPassword()) && passwordPattern.matcher(body.getPassword()).find()) {
            response = this.passwordResetService.updateUserPassword(body.getEmail(), body.getPreviousPassword(), body.getPassword());
        } else {
            response = new AuthEntityResponse();
            response.setMessage("Password requirements: Minimum 12 characters, at least one uppercase letter, one lowercase letter, one number and one special character");
            response.setStatusCode(403);
        }

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("requestPasswordChange")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordResetDTO passwordResetDTO) {
        return ResponseEntity.ok().body(this.passwordResetService.passwordResetRequest(passwordResetDTO));
    }

    @PostMapping("otp")
    public ResponseEntity<?> otp(@RequestBody OtpRequest otp) {
        try {
            String jwt = userService.validateOtp(otp.getOtp());
            System.out.println("Still continued");
            if (Objects.isNull(jwt)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new OtpResponse(null, "Invalid OTP"));
            } else {
                return ResponseEntity.ok().body(new OtpResponse(jwt, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new OtpResponse(null, e.getMessage()));
        }
    }

    @PostMapping("resend-otp")
    public ResponseEntity<?> resendOtp() {
        try {
            String newJwt = this.userService.resendOtp();
            return ResponseEntity.ok(new OtpResponse(newJwt, null));
        } catch (MailServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new OtpResponse(null, e.getMessage()));
        }
    }

    @PostMapping("logout")
    public void logout() {
        userService.logOut();
    }
}



