package ke.co.myfuture.Myfuture.QuestionStore.Curriculum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/read")
@RequiredArgsConstructor
public class CurriculumReadController {

    private final CurriculumRepository curriculumRepository;

    // ⭐ Read homepage
    @GetMapping
    public String listCurriculums(Model model) {
        List<Curriculum> curriculums = curriculumRepository.findByIdNot(1L);
        model.addAttribute("curriculums", curriculums);
        return "read/index";
    }

    // OLD URL: /read/curriculum/{id} → redirect to slug
    @GetMapping("curriculum/{id}")
    public String redirectToSlug(@PathVariable Long id) {

        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum not found"));

        if (curriculum.getSlug() == null || curriculum.getSlug().isBlank()) {
            throw new RuntimeException("Slug missing for curriculum: " + curriculum.getName());
        }

        // Redirect to slug-based URL (301)
        return "redirect:/read/" + curriculum.getSlug();
    }

    // NEW URL: /read/curriculum/{slug}
    @GetMapping("{slug}")
    public String curriculumProfileBySlug(@PathVariable String slug, Model model) {

        Curriculum curriculum = curriculumRepository.findAll()
                .stream()
                .filter(c -> c.getSlug() != null && c.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Curriculum not found for slug: " + slug));

        model.addAttribute("curriculum", curriculum);
        model.addAttribute("curriculums",
                curriculumRepository.getAllCurriculums());

        return "read/curriculum/profile";
    }
}
