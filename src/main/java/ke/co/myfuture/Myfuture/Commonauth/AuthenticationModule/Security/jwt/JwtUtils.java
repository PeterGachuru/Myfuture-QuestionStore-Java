package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.User.UserData;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Otp.OtpService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${users.app.jwtSecret}")
    private String jwtSecret;

    @Value("${users.app.jwtExpirationMs}")
    private int jwtExpirationMs;
    private final OtpService otpService;

    public String generateJwtToken(UserData userData, Boolean ... otp) {
        Map<String, Object> headerData = new HashMap<>();
        long expiration = jwtExpirationMs;

        expiration = 60L * 1000 * 60 * 24*  365 ;

        if (otp.length > 0) {
            headerData.put("otpAuthenticated", false);
            expiration = 60L * 1000 * 60 * 24*  365 ; //whole year hours for the non-otp authenticated token
        }else{
            headerData.put("otpAuthenticated", true);
        }
        return Jwts.builder()
                .setSubject((userData.getEmail()))
                .claim("LOGGED_SYSTEM", userData.getLoggedSystem())
                .claim("LOGGED_DOMAIN", userData.getLoggedDomain())
                .setIssuedAt(new Date())
                .setHeader(headerData)
                .setExpiration(new Date((new Date()).getTime() + expiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public Map<String, Object> getHeadersFromJwtToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getHeader();
    }

    public boolean validateJwtToken(String authToken) {
        try {
//            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return otpService.validateJwtToken(authToken);
        }catch (Exception e){
            System.out.println("error ----- -------- ------- - ");
            log.info(e.getMessage());
        }

        return false;
    }
}
