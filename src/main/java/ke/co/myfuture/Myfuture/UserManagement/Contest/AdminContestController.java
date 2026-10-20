package ke.co.myfuture.Myfuture.UserManagement.Contest;

import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInvitee;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInviteeRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/contests")
public class AdminContestController {

    private final ContestRepository contestRepository;
    private final ContestInviteeRepository contestInviteeRepository;
    private final CurriLevelRepository curriLevelRepository;
    private final SubjectRepository subjectRepository;

    public AdminContestController(ContestRepository contestRepository,
                                  ContestInviteeRepository contestInviteeRepository,
                                  CurriLevelRepository curriLevelRepository,
                                  SubjectRepository subjectRepository) {
        this.contestRepository = contestRepository;
        this.contestInviteeRepository = contestInviteeRepository;
        this.curriLevelRepository = curriLevelRepository;
        this.subjectRepository = subjectRepository;
    }

    // List all contests
    @GetMapping
    public String listContests(Model model) {
        List<Contest> contests = contestRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 300));
        model.addAttribute("contests", contests);
        model.addAttribute("classLevelNames", curriLevelRepository.findAllById(
                        contests.stream().map(c -> c.classlevelId).distinct().toList())
                .stream().collect(Collectors.toMap(CurriLevel::getId,
                        level -> label(level.getName(), level.getId()))));
        model.addAttribute("subjectNames", subjectRepository.findAllById(
                        contests.stream().map(c -> c.subjectId).distinct().toList())
                .stream().collect(Collectors.toMap(Subject::getId,
                        subject -> label(subject.getName(), subject.getId()))));
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

    private static String label(String name, Long id) {
        return name == null || name.isBlank() ? "ID " + id : name + " (ID " + id + ")";
    }
}
