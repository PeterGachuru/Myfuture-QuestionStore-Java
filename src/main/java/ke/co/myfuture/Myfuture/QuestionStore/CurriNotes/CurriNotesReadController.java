package ke.co.myfuture.Myfuture.QuestionStore.CurriNotes;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.LoginSession;
import ke.co.myfuture.Myfuture.Commonauth.Install.Install;
import ke.co.myfuture.Myfuture.Commonauth.Install.Install2Repository;
import ke.co.myfuture.Myfuture.Commonauth.Install.InstallService;
import ke.co.myfuture.Myfuture.Commonauth.Install.WebInstallService;
import ke.co.myfuture.Myfuture.HttpAuth.CookieService;
import ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriNotes.ChatGPTNotesService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.CurriculumRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.PageVisit.PageVisit;
import ke.co.myfuture.Myfuture.UserManagement.PageVisit.PageVisitRepository;
import ke.co.myfuture.Myfuture.UserManagement.SubscriptionExpiryTrack.SubscriptionExpiryTrackRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    private final WebInstallService webInstallService;
    private final InstallService installService;

    private final SubscriptionExpiryTrackRepository subscriptionRepo;
    public final static Integer VISITCOUNT_BEFORE_CLOSE = 5;

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

//        System.out.println("Slug retrieved:  '"+slug+"'");

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

        if (notesOpt.isEmpty()) {
            System.out.println("Could not create notes");
            return "error";
        }

        loadPageData(request, model, topic, notesOpt.get());
        String visitorId = cookieService.getOrCreateVisitorId(request, response);
        // Save the visit
        PageVisit visit = new PageVisit();
        visit.setTopicId(topic.getId());
        visit.setVisitorId(visitorId);
        visit.setVisitTime(LocalDateTime.now());
        visit.setAccessedUri(request.getRequestURI() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
        // ✅ Get User-Agent
        String userAgent = request.getHeader("User-Agent");
        visit.setUserAgent(userAgent);

// (Optional but very useful)
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        visit.setIpAddress(ipAddress);
        pageVisitRepository.save(visit);
        // Count visits
        int visitCount = pageVisitRepository.countByVisitorId(visitorId);

// Check login (if using session)
        LoginSession user = (LoginSession) request.getSession().getAttribute("user");
        boolean loggedIn = user != null;

        IbukaStudentAccount student =
                (IbukaStudentAccount) request.getSession().getAttribute("student");

        boolean hasActiveSubscription = false;

        if (loggedIn && student != null) {
            hasActiveSubscription =
                    subscriptionRepo.existsByParentUsernameAndExpiryDateAfter(
                            user.getEmail(),
                            new Date()
                    );
        }

        boolean restrictForGuest = !loggedIn && visitCount > VISITCOUNT_BEFORE_CLOSE;
        boolean restrictForSubscription = loggedIn && !hasActiveSubscription;

// final restriction
        boolean restrictContent = restrictForGuest || restrictForSubscription;

        model.addAttribute("restrictContent", restrictContent);
        model.addAttribute("restrictForSubscription", restrictForSubscription);
        model.addAttribute("hasActiveSubscription", hasActiveSubscription);

        model.addAttribute("visitCount", visitCount);
        model.addAttribute("visitorId", visitorId);

        if (webInstallService.getInstallId(request) != null) {
            model.addAttribute("installId", webInstallService.getInstallId(request).getId());
            Install install = webInstallService.getInstallId(request);
            if (install.getAccountEmail() == null) {
                installService.addAccountDetails(install, user);
            }
        }


        if (loggedIn && student == null) {
            return "redirect:/students/select";
        }

        return "read/notes/profile";
    }



    // =============================
    // COMMON DATA LOADER
    // =============================
    private void loadPageData(HttpServletRequest request, Model model,
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

//        System.out.println("curriculum: "+ curriculumRepository.findById(topic.getCurriLevel().getCurriculum()).get());

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

        Object user = request.getSession().getAttribute("user");
        boolean loggedIn = user != null;

        model.addAttribute("loggedIn", loggedIn);
        System.out.println("User: "+user);
        model.addAttribute("currentUser", user); // 👈 IMPORTANT
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

    @PostMapping("regenerate")
    public void regenerate(@RequestParam() Long subtopic, @RequestParam String key) {
        if (key.equalsIgnoreCase("peter"))
            chatGPTNotesService.generateNotesForSubtopic("gpt-3.5-turbo-0125", subtopic);
    }
}
