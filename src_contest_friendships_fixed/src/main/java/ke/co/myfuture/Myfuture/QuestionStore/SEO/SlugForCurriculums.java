package ke.co.myfuture.Myfuture.QuestionStore.SEO;


import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.Curriculum;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SlugForCurriculums {

    private final CurriculumRepository curriculumRepository;

    public void generateMissingSlugs() {

        List<Curriculum> curriculums =
                curriculumRepository.findBySlugIsNullOrSlug("");

        for (Curriculum curriculum : curriculums) {

            if (curriculum.getSlug() == null || curriculum.getSlug().isBlank()) {

                String baseSlug = slugify(curriculum.getFullname());
                String uniqueSlug = makeUniqueSlug(baseSlug);

                curriculum.setSlug(uniqueSlug);
                curriculumRepository.save(curriculum);

                System.out.println("Generated slug: " + uniqueSlug);
            }
        }

        System.out.println("Curriculum slug generation complete.");
    }


    private String makeUniqueSlug(String baseSlug) {

        String slug = baseSlug;
        int counter = 1;

        while (curriculumRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }


    private String slugify(String input) {

        if (input == null) return "";

        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")   // remove special chars
                .replaceAll("\\s+", "-")           // spaces to hyphen
                .replaceAll("-+", "-")             // multiple hyphens
                .trim();
    }
}
