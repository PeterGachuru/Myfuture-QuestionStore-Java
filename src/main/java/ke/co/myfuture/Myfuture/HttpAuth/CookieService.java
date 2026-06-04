package ke.co.myfuture.Myfuture.HttpAuth;

import ke.co.myfuture.Myfuture.Commonauth.Install.Install;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;


import ke.co.myfuture.Myfuture.Commonauth.Install.WebInstallService;

@Service
@AllArgsConstructor
public class CookieService {
    private final WebInstallService installService;
    public String getOrCreateVisitorId(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("visitorId".equals(c.getName())) {
                    Install install = installService.getOrCreateInstall(request, response);
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

    public static void addRememberMeCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("REMEMBER_ME", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(12* 30 * 24 * 60 * 60); // 1 year
        response.addCookie(cookie);
    }

    public static String getRememberMeCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("REMEMBER_ME".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
