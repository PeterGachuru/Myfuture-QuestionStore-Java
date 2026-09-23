package ke.co.myfuture.Myfuture.UserManagement.Contest;

import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInvitee;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInviteeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/contests")
public class AdminContestController {

    private final ContestRepository contestRepository;
    private final ContestInviteeRepository contestInviteeRepository;

    public AdminContestController(ContestRepository contestRepository,
                                  ContestInviteeRepository contestInviteeRepository) {
        this.contestRepository = contestRepository;
        this.contestInviteeRepository = contestInviteeRepository;
    }

    // List all contests
    @GetMapping
    public String listContests(Model model) {
        List<Contest> contests = contestRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 300));
        model.addAttribute("contests", contests);
        return "admin/contests";
    }

    // List invitees for a given contest
    @GetMapping("/{id}/invitees")
    public String listInvitees(@PathVariable Long id, Model model) {
        Contest contest = contestRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Contest not found: " + id)
        );

        List<ContestInvitee> invitees = contestInviteeRepository.findByContestOrderByCreatedAtDesc(id);

        model.addAttribute("contest", contest);
        model.addAttribute("invitees", invitees);

        return "admin/contest-invitees";
    }
}