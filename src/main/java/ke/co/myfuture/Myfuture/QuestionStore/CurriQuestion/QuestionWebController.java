package ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion;

import ke.co.myfuture.Myfuture.HttpAuth.CookieService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import ke.co.myfuture.Myfuture.UserManagement.PageVisit.PageVisit;
import ke.co.myfuture.Myfuture.UserManagement.PageVisit.PageVisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/read")
@RequiredArgsConstructor
public class QuestionWebController {
    private final CurriTopicRepository topicRepository;
    private final CurriQuestionRepository questionRepository;
    private final PageVisitRepository pageVisitRepository;
    private final CookieService cookieService;

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
        pageVisitRepository.save(visit);
        model.addAttribute("visitorId", visitorId);

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