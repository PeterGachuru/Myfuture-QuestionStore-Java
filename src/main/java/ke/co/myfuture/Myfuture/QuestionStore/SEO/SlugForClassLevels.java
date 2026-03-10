package ke.co.myfuture.Myfuture.QuestionStore.SEO;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.Curriculum;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SlugForClassLevels {
    private final CurriLevelRepository levelRepository;
    private final CurriculumRepository curriculumRepository;

    public void generateMissingSlugs() {

        List<CurriLevel> levels =
                levelRepository.findBySlugIsNullOrSlug("");

        for (CurriLevel level : levels) {

            if (level.getSlug() == null || level.getSlug().isBlank()) {

                // Fetch parent curriculum
                Optional<Curriculum> curriculumOpt =
                        curriculumRepository.findById(level.getCurriculum());

                if (curriculumOpt.isEmpty()) {
                    System.out.println("Curriculum not found for level: " + level.getName());
                    continue;
                }

                String curriculumSlug = curriculumOpt.get().getSlug();

                if (curriculumSlug == null || curriculumSlug.isBlank()) {
                    System.out.println("Curriculum slug missing, generate it first for: " + curriculumOpt.get().getName());
                    continue;
                }

                // Build slug: curriculumSlug/classLevelSlug
                String baseSlug = curriculumSlug + "/" + slugify(level.getName());
                String uniqueSlug = makeUniqueSlug(baseSlug);

                level.setSlug(uniqueSlug);
                levelRepository.save(level);

                System.out.println("Generated class level slug: " + uniqueSlug);
            }
        }

        System.out.println("Class level slug generation complete.");
    }

    private String makeUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int counter = 1;

        while (levelRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    private String slugify(String input) {
        if (input == null) return "";

        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")  // remove special chars
                .replaceAll("\\s+", "-")          // spaces → hyphen
                .replaceAll("-+", "-")            // merge multiple hyphens
                .trim();
    }
}
