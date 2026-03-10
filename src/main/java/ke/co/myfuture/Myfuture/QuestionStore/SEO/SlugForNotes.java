package ke.co.myfuture.Myfuture.QuestionStore.SEO;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNotes.CurriNotesRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.CurriculumRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SlugForNotes {
    @Autowired
    private CurriculumRepository curriculumRepository;
    @Autowired
    private CurriLevelRepository levelRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CurriTopicRepository topicRepository;

    @Autowired
    private CurriNotesRepository notesRepository;

    @Transactional
    public void generateMissingSlugs() {

        topicRepository.findBySlugIsNullOrSlugIs("").forEach(topic -> {

            if (topic != null) {
                System.out.println("========================================================");
                String baseSlug = buildSlugForTopic(topic);

                String uniqueSlug = makeSlugUnique(baseSlug, topic.getId());

                topic.setSlug(uniqueSlug);
                System.out.println(uniqueSlug);

                topicRepository.save(topic);
            }
        });

        System.out.println("Missing slugs generated for topics/subtopics.");
    }


    private String makeSlugUnique(String baseSlug, Long topicId) {

        String slug = baseSlug;
        int counter = 2;

        while (topicRepository.existsBySlugAndIdNot(slug, topicId)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }


    private String buildSlugForTopic(CurriTopic topic) {
        StringBuilder sb = new StringBuilder();

        // Get parent topics recursively (if any)
        buildParentSlug(topic, sb);

        // Add current topic name
        if (!sb.isEmpty()) sb.append("/");
        sb.append(slugify(topic.getName()));
        System.out.println("Topic name: "+topic.getName());

        return sb.toString();
    }

    private void buildParentSlug(CurriTopic topic, StringBuilder sb) {
        System.out.println("buildParentSlug");
        if (topic != null && topic.getParent() != null) {
            buildParentSlug(topic.getParent(), sb); // recursion
            System.out.println("sb: "+sb);
            if (!sb.isEmpty()) {
                System.out.println("added separator");
                sb.append("/");
            }
            System.out.println("Parent name: "+topic.getParent().getName());
            sb.append(slugify(topic.getParent().getName()));
        }else {
            System.out.println("No parent");
        }
    }

    // Convert string to SEO-friendly slug
    private String slugify(String input) {
        if (input == null) return "";
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")  // remove special chars
                .replaceAll("\\s+", "-")          // replace spaces with hyphen
                .replaceAll("-+", "-")            // merge multiple hyphens
                .trim();
    }
}
