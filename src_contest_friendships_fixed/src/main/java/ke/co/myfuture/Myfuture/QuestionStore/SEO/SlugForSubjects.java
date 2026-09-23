package ke.co.myfuture.Myfuture.QuestionStore.SEO;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.Curriculum;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.CurriculumRepository;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevel;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SlugForSubjects {

    private final SubjectLevelRepository subjectLevelRepository;
    private final CurriLevelRepository levelRepository;
    private final CurriculumRepository curriculumRepository;

    public void generateMissingSlugs() {

        // Get all subjects without slug
        List<SubjectLevel> subjectLevels = subjectLevelRepository.findAll()
                .stream()
                .filter(s -> s.getSlug() == null || s.getSlug().isBlank())
                .toList();

        for (SubjectLevel subjectLevel : subjectLevels) {

            // Find a class level for this subject
            Optional<CurriLevel> levelOpt = levelRepository.findById(subjectLevel.getCurriLevel().getId());

            if (levelOpt.isEmpty()) {
                System.out.println("ClassLevel not found for subject: " + subjectLevel.getSubject().getName());
                continue;
            }

            CurriLevel level = levelOpt.get();

            // Get parent curriculum
            Optional<Curriculum> curriculumOpt = curriculumRepository.findById(level.getCurriculum());

            if (curriculumOpt.isEmpty()) {
                System.out.println("Curriculum not found for level: " + level.getName());
                continue;
            }

            Curriculum curriculum = curriculumOpt.get();

            if (curriculum.getSlug() == null || curriculum.getSlug().isBlank()) {
                System.out.println("Curriculum slug missing for: " + curriculum.getName() + " – generate first!");
                continue;
            }

            if (level.getSlug() == null || level.getSlug().isBlank()) {
                System.out.println("ClassLevel slug missing for: " + level.getName() + " – generate first!");
                continue;
            }

            // Build slug: curriculumSlug / levelSlug / subjectSlug
            String baseSlug = level.getSlug() + "/" + slugify(subjectLevel.getSubject().getName());
            String uniqueSlug = makeUniqueSlug(baseSlug);

            subjectLevel.setSlug(uniqueSlug);
            subjectLevelRepository.save(subjectLevel);

            System.out.println("Generated subject slug: " + uniqueSlug);
        }

        System.out.println("Subject slug generation complete.");
    }

    private String makeUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int counter = 1;

        while (subjectLevelRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    private String slugify(String input) {
        if (input == null) return "";

        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")   // remove special chars
                .replaceAll("\\s+", "-")           // spaces → hyphen
                .replaceAll("-+", "-")             // merge multiple hyphens
                .trim();
    }
}
