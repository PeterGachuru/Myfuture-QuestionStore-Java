package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/topics")
public class AdminTopicController {

    private final CurriTopicRepository curriTopicRepository;

    public AdminTopicController(CurriTopicRepository curriTopicRepository) {
        this.curriTopicRepository = curriTopicRepository;
    }

    @GetMapping
    public String listTopics(Model model) {

        List<CurriTopic> topics =
                curriTopicRepository.findByDeletedFalseOrderByCreatedAtDesc(PageRequest.of(0, 300));

        model.addAttribute("topics", topics);

        return "admin/topics";
    }
}