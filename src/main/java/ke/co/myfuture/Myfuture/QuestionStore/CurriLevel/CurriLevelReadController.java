package ke.co.myfuture.Myfuture.QuestionStore.CurriLevel;

import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.Curriculum;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.CurriculumRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/read/classlevel")
public class CurriLevelReadController {

    private final CurriLevelRepository levelRepository;
    private final CurriculumRepository curriculumRepository;
    private final SubjectRepository subjectRepository;

    public CurriLevelReadController(
            CurriLevelRepository levelRepository,
            CurriculumRepository curriculumRepository,
            SubjectRepository subjectRepository) {

        this.levelRepository = levelRepository;
        this.curriculumRepository = curriculumRepository;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping("/{id}")
    public String levelProfile(@PathVariable Long id, Model model) {

        CurriLevel level = levelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found"));

        // Load subjects into the transient field
        level.setSubjects(subjectRepository.subjectsByClassLevel(id));

        // Load curriculum for backlink
        Curriculum curriculum = curriculumRepository.findById(level.getCurriculum())
                .orElse(null);

        model.addAttribute("level", level);
        model.addAttribute("curriculum", curriculum);

        return "read/classlevel/profile";
    }
}

