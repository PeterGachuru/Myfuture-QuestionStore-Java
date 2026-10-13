package ke.co.myfuture.Myfuture.Commonauth.WebAdminAuth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StudentWebHandoffService {

    public enum Action {
        CREATE, ATTEMPT, DETAILS, CONTESTS
    }

    private final String jwtSecret;

    public StudentWebHandoffService(
            @Value("${users.app.jwtSecret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public String createLink(String email, Long studentId, Action action, Long resourceId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + (2 * 60 * 1000));

        String token = Jwts.builder()
                .setSubject(email)
                .claim("studentId", studentId)
                .claim("action", action.name())
                .claim("resourceId", resourceId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        return "/read/student-handoff?token=" +
                java.net.URLEncoder.encode(token, java.nio.charset.StandardCharsets.UTF_8);
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }
}
