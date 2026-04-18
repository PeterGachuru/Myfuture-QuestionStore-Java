package ke.co.myfuture.Myfuture.HttpAuth;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class CookieService {
    public String getOrCreateVisitorId(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("visitorId".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }

        String visitorId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("visitorId", visitorId);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 365); // 1 year
        response.addCookie(cookie);
        return visitorId;
    }

    public String getVisitorId(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("visitorId".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }

        return null;
    }
}
