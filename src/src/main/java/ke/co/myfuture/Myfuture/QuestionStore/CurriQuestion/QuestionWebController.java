package ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.LoginSession;
import ke.co.myfuture.Myfuture.HttpAuth.CookieService;
import ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriQuestion.ChatGPTQuestionsService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.PageVisit.PageVisit;
import ke.co.myfuture.Myfuture.UserManagement.PageVisit.PageVisitRepository;
import ke.co.myfuture.Myfuture.UserManagement.SubscriptionExpiryTrack.SubscriptionExpiryTrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ke.co.myfuture.Myfuture.QuestionStore.CurriNotes.CurriNotesReadController.VISITCOUNT_BEFORE_CLOSE;

@Controller
@RequestMapping("/read")
@RequiredArgsConstructor
public class QuestionWebController {
    private final CurriTopicRepository topicRepository;
    private final CurriQuestionRepository questionRepository;
    private final PageVisitRepository pageVisitRepository;
    private final CookieService cookieService;
    private final SubscriptionExpiryTrackRepository subscriptionRepo;
    private final ChatGPTQuestionsService chatGPTQuestionsService;

    // SEO-friendly URL
    @GetMapping("/questions/**")
    public String getQuestionsBySlug(HttpServletRequest request,
                                     Model model,
                                     HttpServletResponse response) {

        String fullPath = request.getRequestURI();
        String slug = fullPath.substring(fullPath.indexOf("/read/questions/") + 16);

        Optional<CurriTopic> topicOpt = topicRepository.findBySlug(slug);
        if (topicOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "error";
        }

        CurriTopic topic = topicOpt.get();

        List<CurriQuestion> questions = questionRepository.findBySubtopic(topic.getId());
        if (questions.isEmpty()) {
            chatGPTQuestionsService.generateForSubtopic(topic);
        }
        for (CurriQuestion q : questions) {
            Collections.shuffle(q.getChoices());
        }
        model.addAttribute("questions", questions);
        model.addAttribute("topic", topic);

        // Sidebar topics
        List<CurriTopic> parentTopics =
                topicRepository.findBySubjectAndClass(topic.getSubject().getId(), topic.getCurriLevel().getId());
        parentTopics.forEach(parent -> parent.setChildren(topicRepository.findByParent(parent.getId())));
        model.addAttribute("parentTopics", parentTopics);
        model.addAttribute("level", topic.getCurriLevel());
        model.addAttribute("subject", topic.getSubject());

        // Track visitor
        String visitorId = cookieService.getOrCreateVisitorId(request, response);
        PageVisit visit = new PageVisit();
        visit.setTopicId(topic.getId());
        visit.setVisitorId(visitorId);
        visit.setVisitTime(LocalDateTime.now());
        visit.setAccessedUri(request.getRequestURI() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
        // (Optional but very useful)
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        visit.setIpAddress(ipAddress);
        pageVisitRepository.save(visit);
        // Count visits
        int visitCount = pageVisitRepository.countByVisitorId(visitorId);
        model.addAttribute("visitorId", visitorId);


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

// Restriction logic
        boolean restrictForSubscription = loggedIn && !hasActiveSubscription;

// (Optional: if you also want guest limiting like notes page)
        boolean restrictForGuest = !loggedIn && visitCount > VISITCOUNT_BEFORE_CLOSE;

        boolean restrictContent = restrictForSubscription || restrictForGuest;

// Pass to view
        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("restrictContent", restrictContent);
        model.addAttribute("restrictForSubscription", restrictForSubscription);

        return "read/quizes/subtopic";  // Thymeleaf page
    }

    // Redirect old ID-based URL to SEO-friendly slug
    @GetMapping("/topic/{topicId}/questions")
    public void redirectOldUrl(@PathVariable Long topicId, HttpServletResponse response) throws IOException {
        Optional<CurriTopic> topicOpt = topicRepository.findById(topicId);
        if (topicOpt.isPresent()) {
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", "/read/questions/" + topicOpt.get().getSlug());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}