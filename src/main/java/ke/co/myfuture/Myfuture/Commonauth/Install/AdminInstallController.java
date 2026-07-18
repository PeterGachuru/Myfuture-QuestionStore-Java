package ke.co.myfuture.Myfuture.Commonauth.Install;

import ke.co.myfuture.Myfuture.UserManagement.ClicksAnalyticsEvent.AnalyticsEvent;
import ke.co.myfuture.Myfuture.UserManagement.ClicksAnalyticsEvent.AnalyticsRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/installs")
@AllArgsConstructor
public class AdminInstallController {

    private final Install2Repository installRepository;

    private final AnalyticsRepository analyticsRepository;

    @GetMapping
    public String list(
            @RequestParam(required = false) String platform,
            Model model) {

        if (platform != null && !platform.isEmpty()) {
            model.addAttribute("installs",
                    installRepository.findTop500ByPlatformOrderByCreatedAtDesc(platform));
        } else {
            model.addAttribute("installs",
                    installRepository.findLatest500());
        }

        model.addAttribute("selectedPlatform", platform);

        return "admin/installs";
    }

//    @GetMapping("/{id}")
//    public String install(@PathVariable Long id, Model model) {
//        model.addAttribute("install",
//                installRepository.findById(id).orElseThrow());
//
//        return "admin/install";
//    }

    @GetMapping("/{id}")
    public String profile(@PathVariable Long id, Model model) {

        Install install = installRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Install not found"));

        List<AnalyticsEvent> events =
                analyticsRepository.findTop3000ByInstallIdOrderByEventTimeDesc(
                        install.id
                );

        model.addAttribute("install", install);
        model.addAttribute("events", events);

        return "admin/install-profile";
    }
}