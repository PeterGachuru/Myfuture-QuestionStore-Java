package ke.co.myfuture.Myfuture.QuestionStore.Curriculum;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/read")
public class CurriculumReadController {

    private final CurriculumRepository curriculumRepository;

    public CurriculumReadController(CurriculumRepository curriculumRepository) {
        this.curriculumRepository = curriculumRepository;
    }

    // ⭐ NEW: Read homepage
    @GetMapping
    public String listCurriculums(Model model) {

        List<Curriculum> curriculums = curriculumRepository.findAll();

        model.addAttribute("curriculums", curriculums);

        return "read/index";
    }

    @GetMapping("curriculum/{id}")
    public String curriculumProfile(@PathVariable Long id, Model model) {

        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum not found"));

        model.addAttribute("curriculum", curriculum);

        return "read/curriculum/profile";
    }
}