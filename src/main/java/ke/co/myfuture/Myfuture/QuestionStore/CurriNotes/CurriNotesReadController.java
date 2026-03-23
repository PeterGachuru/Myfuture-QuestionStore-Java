package ke.co.myfuture.Myfuture.QuestionStore.CurriNotes;

import ke.co.myfuture.Myfuture.HttpAuth.CookieService;
import ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriNotes.ChatGPTNotesService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.CurriculumRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import ke.co.myfuture.Myfuture.UserManagement.PageVisit.PageVisit;
import ke.co.myfuture.Myfuture.UserManagement.PageVisit.PageVisitRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/read")
@AllArgsConstructor
public class CurriNotesReadController {

    private final CurriTopicRepository curriTopicRepository;
    private final CurriNotesRepository notesRepository;

    private final PageVisitRepository pageVisitRepository;

    private final  ChatGPTNotesService chatGPTNotesService;

    private final SubjectRepository subjectRepository;

    private final CurriLevelRepository curriLevelRepository;

    private final CurriculumRepository curriculumRepository;
    private final CookieService cookieService;

    // =============================
    // NEW SEO URL
    // =============================
    @GetMapping("/notes/**")
    public String readNotesBySlug(HttpServletRequest request,
                                  Model model,
                                  HttpServletResponse response) {

        String fullPath = request.getRequestURI();

        // Extract slug after /read/notes/
        String slug = fullPath.substring(fullPath.indexOf("/read/notes/") + 12);

        System.out.println("Slug retrieved:  '"+slug+"'");

        Optional<CurriTopic> topicOpt = curriTopicRepository.findBySlug(slug);

        if (topicOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            System.out.println("Topic by slug not found");
            return "error";
        }

        CurriTopic topic = topicOpt.get();

        Optional<CurriNotes> notesOpt =
                notesRepository.findBySubtopicIdAndDeletedFlagFalse(topic.getId());


        if (topic.getCurriLevel().getCurriculum() == 1) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            System.out.println("Topic is from KCPE");
            return "error";
        }

        if (notesOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            System.out.println("Topic notes not found");

            chatGPTNotesService.generateNotesForSubtopic("gpt-3.5-turbo-0125", topic.getId());
            notesOpt =
                    notesRepository.findBySubtopicIdAndDeletedFlagFalse(topic.getId());
//            return "error";
        }

        loadPageData(model, topic, notesOpt.get());
        String visitorId = cookieService.getOrCreateVisitorId(request, response);
        // Save the visit
        PageVisit visit = new PageVisit();
        visit.setTopicId(topic.getId());
        visit.setVisitorId(visitorId);
        visit.setVisitTime(LocalDateTime.now());
        pageVisitRepository.save(visit);
        model.addAttribute("visitorId", visitorId);

        return "read/notes/profile";
    }



    // =============================
    // COMMON DATA LOADER
    // =============================
    private void loadPageData(Model model,
                              CurriTopic topic,
                              CurriNotes notes) {

        List<CurriTopic> parentTopics =
                curriTopicRepository.findBySubjectAndClass(
                        topic.getSubject().getId(),
                        topic.getCurriLevel().getId()
                );

        parentTopics.forEach(parent ->
                parent.setChildren(curriTopicRepository.findByParent(parent.getId()))
        );

        model.addAttribute("topic", topic);
        model.addAttribute("parentTopic", topic.getParent());
        model.addAttribute("notes", notes);
        model.addAttribute("parentTopics", parentTopics);
        model.addAttribute("level", topic.getCurriLevel());
        model.addAttribute("subject", topic.getSubject());
        model.addAttribute("curriculum", curriculumRepository.findById(topic.getCurriLevel().getCurriculum()).get());

        System.out.println("curriculum: "+ curriculumRepository.findById(topic.getCurriLevel().getCurriculum()).get());


        model.addAttribute("allLevels",
                curriLevelRepository.getAllByCurriculum(topic.getCurriLevel().getCurriculum()));
        model.addAttribute("curriculums",
                curriculumRepository.getAllCurriculums());

        // Load subjects
        List<Subject> subjects = subjectRepository.subjectsByClassLevel(topic.getCurriLevel().id);

        // For each subject, fetch the first subtopic ID
        subjects.forEach(subject -> {
            List<CurriTopic> topics = curriTopicRepository.findBySubjectAndClass(subject.getId(), topic.getCurriLevel().id);
            if (!topics.isEmpty()) {
                CurriTopic firstParent = topics.get(0);
                List<CurriTopic> children = curriTopicRepository.findByParent(firstParent.getId());
                CurriTopic firstSubtopic = children.isEmpty() ? firstParent : children.get(0);

                // Add transient field to Subject for template
                subject.setFirstSubtopicSlug(firstSubtopic.getSlug());
            }
        });



        model.addAttribute("allSubjects", subjects);
    }


    // =============================
    // OLD URL → REDIRECT TO NEW
    // =============================
    @GetMapping("/topic/{topicId}/notes")
    public String redirectOldUrl(@PathVariable Long topicId,
                                 HttpServletResponse response) throws IOException {

        Optional<CurriTopic> topicOpt = curriTopicRepository.findById(topicId);

        if (topicOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "error";
        }

        CurriTopic topic = topicOpt.get();

        String newUrl = "/read/notes/" + topic.getSlug();

        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY); // 301
        response.setHeader("Location", newUrl);

        return null;
    }

}
