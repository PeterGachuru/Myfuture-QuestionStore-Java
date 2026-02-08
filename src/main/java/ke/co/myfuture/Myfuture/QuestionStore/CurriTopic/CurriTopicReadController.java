package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/read/level/{levelId}/subject/{subjectId}/topics")
public class CurriTopicReadController {

    private final CurriTopicRepository topicRepository;
    private final CurriLevelRepository levelRepository;
    private final SubjectRepository subjectRepository;

    public CurriTopicReadController(
            CurriTopicRepository topicRepository,
            CurriLevelRepository levelRepository,
            SubjectRepository subjectRepository) {
        this.topicRepository = topicRepository;
        this.levelRepository = levelRepository;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping
    public String listTopics(
            @PathVariable Long levelId,
            @PathVariable Long subjectId,
            Model model) {

        CurriLevel level = levelRepository.findById(levelId)
                .orElseThrow(() -> new RuntimeException("Level not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // Fetch all parent topics (parent IS NULL)
        List<CurriTopic> parentTopics = topicRepository.findBySubjectAndClass(subjectId, levelId);

        // Optionally, fetch children for each parent
        parentTopics.forEach(parent -> {
            List<CurriTopic> children = topicRepository.findByParent(parent.getId());
            parent.setChildren(children); // We need a transient `children` field in CurriTopic
        });

        model.addAttribute("level", level);
        model.addAttribute("subject", subject);
        model.addAttribute("parentTopics", parentTopics);

        return "read/topic/list";
    }
}
