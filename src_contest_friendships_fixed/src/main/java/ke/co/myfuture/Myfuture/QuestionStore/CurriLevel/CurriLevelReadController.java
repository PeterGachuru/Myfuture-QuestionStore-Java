package ke.co.myfuture.Myfuture.QuestionStore.CurriLevel;

import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.Curriculum;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.CurriculumRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/read/classlevel")
@RequiredArgsConstructor
public class CurriLevelReadController {

    private final CurriLevelRepository curriLevelRepository;
    private final CurriculumRepository curriculumRepository;
    private final SubjectRepository subjectRepository;
    private final CurriTopicRepository curriTopicRepository;

    // OLD URL: /read/classlevel/{id}
    @GetMapping("/{id}")
    public String redirectToSlug(@PathVariable Long id) {

        CurriLevel level = curriLevelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found"));

        // Ensure slug exists
        if (level.getSlug() == null || level.getSlug().isBlank()) {
            throw new RuntimeException("Slug missing for class level: " + level.getName());
        }

        // Redirect (301) to new slug URL
        return "redirect:/read/classlevel/" + level.getSlug();
    }

    // NEW slug-based URL: /read/classlevel/{curriculumSlug}/{levelSlug}
    @GetMapping("/{curriculumSlug}/{levelSlug}")
    public String levelProfileBySlug(@PathVariable String curriculumSlug,
                                     @PathVariable String levelSlug,
                                     Model model) {

        // Find level by slug
        Optional<CurriLevel> levelOpt = curriLevelRepository.findBySlug(curriculumSlug + "/" + levelSlug);

        if (levelOpt.isEmpty()) {
            return "error";
        }

        CurriLevel level = levelOpt.get();

        // Load subjects
        List<Subject> subjects = subjectRepository.subjectsByClassLevel(level.getId());

        // For each subject, fetch the first subtopic ID
        subjects.forEach(subject -> {
            List<CurriTopic> topics = curriTopicRepository.findBySubjectAndClass(subject.getId(), level.getId());
            if (!topics.isEmpty()) {
                CurriTopic firstParent = topics.get(0);
                List<CurriTopic> children = curriTopicRepository.findByParent(firstParent.getId());
                CurriTopic firstSubtopic = children.isEmpty() ? firstParent : children.get(0);

                // Add transient field to Subject for template
                subject.setFirstSubtopicSlug(firstSubtopic.getSlug());
            }
        });

        level.setSubjects(subjects);

        // Load curriculum
        Curriculum curriculum = curriculumRepository.findById(level.getCurriculum())
                .orElse(null);

        model.addAttribute("level", level);
        model.addAttribute("curriculum", curriculum);
        model.addAttribute("allLevels",
                curriLevelRepository.getAllByCurriculum(level.getCurriculum()));
        model.addAttribute("curriculums",
                curriculumRepository.getAllCurriculums());

        return "read/classlevel/profile";
    }
}
