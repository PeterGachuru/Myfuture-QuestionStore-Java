package ke.co.myfuture.Myfuture.UserManagement.Referral;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/referrals")
public class AdminReferralController {

    private final ReferralRepository repository;

    public AdminReferralController(ReferralRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {

        List<Referral> referrals =
                repository.findAllByOrderByIdDesc(PageRequest.of(0,300));

        model.addAttribute("referrals", referrals);

        return "admin/referrals";
    }

}