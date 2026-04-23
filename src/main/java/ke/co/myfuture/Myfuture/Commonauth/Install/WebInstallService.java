package ke.co.myfuture.Myfuture.Commonauth.Install;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ke.co.myfuture.Myfuture.QuestionStore.Install.InstallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebInstallService {
    @Autowired
    private Install2Repository installRepository;

    public Install getOrCreateInstall(HttpServletRequest request,
                                      HttpServletResponse response) {

        // 1. Check cookie
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("installId".equals(c.getName())) {
                    try {
                        Long id = Long.parseLong(c.getValue());
                        return installRepository.findById(id).orElse(null);
                    } catch (Exception ignored) {}
                }
            }
        }

        // 2. Create new install
        Install install = new Install();
        install.setPlatform("study.myfuture.co.ke");
        install.setVersion(1);

        install = installRepository.save(install);

        // 3. Save cookie
        Cookie cookie = new Cookie("installId", String.valueOf(install.getId()));
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 365); // 1 year
        response.addCookie(cookie);

        return install;
    }
}
