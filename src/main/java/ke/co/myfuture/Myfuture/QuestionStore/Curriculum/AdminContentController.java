package ke.co.myfuture.Myfuture.QuestionStore.Curriculum;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNotes.CurriNotesRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestionRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevel;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/admin/content")
public class AdminContentController {
    private static final int PAGE_SIZE = 25;
    private final CurriculumRepository curriculumRepository;
    private final CurriLevelRepository curriLevelRepository;
    private final SubjectLevelRepository subjectLevelRepository;
    private final CurriTopicRepository curriTopicRepository;
    private final CurriNotesRepository curriNotesRepository;
    private final CurriQuestionRepository curriQuestionRepository;

    public AdminContentController(CurriculumRepository curriculumRepository, CurriLevelRepository curriLevelRepository,
                                  SubjectLevelRepository subjectLevelRepository, CurriTopicRepository curriTopicRepository,
                                  CurriNotesRepository curriNotesRepository, CurriQuestionRepository curriQuestionRepository) {
        this.curriculumRepository = curriculumRepository;
        this.curriLevelRepository = curriLevelRepository;
        this.subjectLevelRepository = subjectLevelRepository;
        this.curriTopicRepository = curriTopicRepository;
        this.curriNotesRepository = curriNotesRepository;
        this.curriQuestionRepository = curriQuestionRepository;
    }

    @GetMapping
    public String curricula(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("results", curriculumRepository.findAllByOrderByCreatedAtDescIdDesc(pageable(page)));
        return "admin/content_curricula";
    }

    @GetMapping("/curricula/{curriculumId}")
    public String classLevels(@PathVariable Long curriculumId, @RequestParam(defaultValue = "0") int page, Model model) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId).orElseThrow(() -> notFound("Curriculum"));
        model.addAttribute("curriculum", curriculum);
        model.addAttribute("results", curriLevelRepository.findByCurriculumOrderByNumberingAscIdAsc(curriculumId, pageable(page)));
        return "admin/content_class_levels";
    }

    @GetMapping("/class-levels/{levelId}")
    public String subjects(@PathVariable Long levelId, @RequestParam(defaultValue = "0") int page, Model model) {
        CurriLevel level = curriLevelRepository.findById(levelId).orElseThrow(() -> notFound("Class level"));
        Curriculum curriculum = curriculumRepository.findById(level.getCurriculum()).orElseThrow(() -> notFound("Curriculum"));
        model.addAttribute("curriculum", curriculum);
        model.addAttribute("level", level);
        model.addAttribute("results", subjectLevelRepository.findByCurriLevelIdAndDeletedFlagFalseOrderByCreatedAtDescIdDesc(levelId, pageable(page)));
        return "admin/content_subjects";
    }

    @GetMapping("/class-levels/{levelId}/subjects/{subjectId}")
    public String topics(@PathVariable Long levelId, @PathVariable Long subjectId, @RequestParam(defaultValue = "0") int page, Model model) {
        CurriLevel level = curriLevelRepository.findById(levelId).orElseThrow(() -> notFound("Class level"));
        Curriculum curriculum = curriculumRepository.findById(level.getCurriculum()).orElseThrow(() -> notFound("Curriculum"));
        SubjectLevel subjectLevel = subjectLevelRepository.findBySubjectIdAndCurriLevelIdAndDeletedFlagFalse(subjectId, levelId)
                .orElseThrow(() -> notFound("Subject for class level"));
        model.addAttribute("curriculum", curriculum);
        model.addAttribute("level", level);
        model.addAttribute("subjectLevel", subjectLevel);
        model.addAttribute("results", curriTopicRepository.findByCurriLevelIdAndSubjectIdAndDeletedFalseOrderByCreatedAtDescIdDesc(levelId, subjectId, pageable(page)));
        return "admin/content_topics";
    }

    @GetMapping("/topics/{topicId}/notes")
    public String notes(@PathVariable Long topicId, @RequestParam(defaultValue = "0") int page, Model model) {
        CurriTopic topic = getTopic(topicId);
        addTopicContext(topic, model);
        model.addAttribute("results", curriNotesRepository.findBySubtopicIdOrderByCreatedAtDescIdDesc(topicId, pageable(page)));
        model.addAttribute("contentType", "Notes");
        return "admin/content_topic_items";
    }

    @GetMapping("/topics/{topicId}/questions")
    public String questions(@PathVariable Long topicId, @RequestParam(defaultValue = "0") int page, Model model) {
        CurriTopic topic = getTopic(topicId);
        addTopicContext(topic, model);
        model.addAttribute("results", curriQuestionRepository.findAdminQuestionsByTopic(topicId, pageable(page)));
        model.addAttribute("contentType", "Questions");
        return "admin/content_topic_items";
    }

    private CurriTopic getTopic(Long id) {
        return curriTopicRepository.findById(id).orElseThrow(() -> notFound("Topic"));
    }

    private void addTopicContext(CurriTopic topic, Model model) {
        model.addAttribute("topic", topic);
        model.addAttribute("level", topic.getCurriLevel());
        model.addAttribute("subject", topic.getSubject());
        model.addAttribute("curriculum", curriculumRepository.findById(topic.getCurriLevel().getCurriculum())
                .orElseThrow(() -> notFound("Curriculum")));
    }

    private Pageable pageable(int page) {
        return PageRequest.of(Math.max(0, page), PAGE_SIZE, Sort.unsorted());
    }

    private ResponseStatusException notFound(String entity) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, entity + " not found");
    }
}
