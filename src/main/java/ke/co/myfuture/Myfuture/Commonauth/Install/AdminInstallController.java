package ke.co.myfuture.Myfuture.Commonauth.Install;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/installs")
@AllArgsConstructor
public class AdminInstallController {

    private final Install2Repository installRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("installs",
                installRepository.findLatest500());

        return "admin/installs";
    }

    @GetMapping("/{id}")
    public String install(@PathVariable Long id, Model model) {
        model.addAttribute("install",
                installRepository.findById(id).orElseThrow());

        return "admin/install";
    }
}