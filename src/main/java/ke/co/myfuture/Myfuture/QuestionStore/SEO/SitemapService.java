package ke.co.myfuture.Myfuture.QuestionStore.SEO;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNotes.CurriNotes;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNotes.CurriNotesRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.CurriculumRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevel;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class SitemapService {

    private final CurriculumRepository curriculumRepository;
    private final CurriLevelRepository levelRepository;
    private final SubjectLevelRepository subjectLevelRepository;
    private final SubjectRepository subjectRepository;
    private final CurriTopicRepository topicRepository;

    private final CurriNotesRepository notesRepository;

    @Autowired
    SlugForNotes slugForNotes;

    @Autowired
    SlugForCurriculums slugForCurriculums;
    @Autowired
    SlugForClassLevels slugForClassLevels;
    @Autowired
    SlugForSubjects slugForSubjects;

//     slugForCurriculums.generateMissingSlugs();
//        slugForClassLevels.generateMissingSlugs();
//        slugForSubjects.generateMissingSlugs();

    // ⭐ change to your real domain
    private final String BASE_URL = "https://study.myfuture.co.ke";

    private final Path sitemapFile = Paths.get("readsitemap.xml");

    // Scheduled generation: 20 sec after start, then every 5 hours
    @PostConstruct
    public void scheduleSitemapGeneration() {
        System.out.println("scheduleSitemapGeneration");
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                generateAndWriteSitemap();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 5, 5 * 3600, TimeUnit.SECONDS); // 5 hours = 5*3600 sec
    }

    // Generate sitemap XML and write to file
    public void generateAndWriteSitemap() throws IOException {

        System.out.println("generateAndWriteSitemap");
        String xml = generateSitemapXml();

        System.out.println("Generated: writing");
        Files.writeString(sitemapFile, xml, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("Sitemap written to " + sitemapFile.toAbsolutePath());
    }

    // Serve XML content (read from file)
    public String getSitemapContent() throws IOException {
        if (!Files.exists(sitemapFile)) {
            generateAndWriteSitemap(); // fallback: generate if file missing
        }
        return Files.readString(sitemapFile);
    }

    public SitemapService(
            CurriculumRepository curriculumRepository,
            CurriLevelRepository levelRepository,
            SubjectLevelRepository subjectLevelRepository,
            SubjectRepository subjectRepository,
            CurriTopicRepository topicRepository,
            CurriNotesRepository notesRepository) {

        this.curriculumRepository = curriculumRepository;
        this.levelRepository = levelRepository;
        this.subjectLevelRepository = subjectLevelRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.notesRepository = notesRepository;
    }

    public String generateSitemapXml() {
        generateMissingSlugs();

        StringBuilder xml = new StringBuilder();

        xml.append("""
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                """);

        // Root
        addUrl(xml, BASE_URL + "/read");

        // Curriculums
        curriculumRepository.findAll()
                .forEach(curriculum -> {
                    if (curriculum.id == 1)
                        return;
                    addUrl(xml, BASE_URL + "/read/" + curriculum.getSlug());

                    // Levels
                    levelRepository.getAllByCurriculum(curriculum.getId())
                            .forEach(level -> {

                                addUrl(xml, BASE_URL + "/read/classlevel/" + level.getSlug());

                                // Subjects
                                subjectRepository.subjectsByClassLevel(level.getId())
                                        .forEach(subject -> {
                                            topicRepository.findAllTopicsByClassLevel(level.getId())
                                                    .forEach(topic -> {

                                                        Optional<CurriNotes> notesOpt =
                                                                notesRepository.findBySubtopicIdAndDeletedFlagFalse(topic.getId());

                                                        if (notesOpt.isPresent()) {
                                                            addUrl(xml,
                                                                    BASE_URL + "/read/notes/"
                                                                            + topic.getSlug());
                                                        }
                                                    });
                                        });
                            });
                });

        xml.append("</urlset>");

        return xml.toString();
    }

    private void generateMissingSlugs() {
        slugForCurriculums.generateMissingSlugs();
        slugForClassLevels.generateMissingSlugs();
        slugForSubjects.generateMissingSlugs();
        slugForNotes.generateMissingSlugs();
    }

    private void addUrl(StringBuilder xml, String url) {
        xml.append("<url>");
        xml.append("<loc>").append(url).append("</loc>");
        xml.append("<changefreq>weekly</changefreq>");
        xml.append("<priority>0.8</priority>");
        xml.append("</url>");
    }

}
