package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@Controller
@RequestMapping("/read/level/{levelId}/subject/{subjectId}/topics")
@RequiredArgsConstructor
public class CurriTopicReadController {

    private final CurriTopicRepository topicRepository;

    @GetMapping
    public String redirectToFirstSubtopic(
            @PathVariable Long levelId,
            @PathVariable Long subjectId) {

        // Fetch all parent topics
        List<CurriTopic> parentTopics = topicRepository.findBySubjectAndClass(subjectId, levelId);

        if (parentTopics.isEmpty()) {
            throw new RuntimeException("No topics found for subject " + subjectId + " and level " + levelId);
        }

        // Pick first parent topic
        CurriTopic firstParent = parentTopics.get(0);

        // Fetch first subtopic
        List<CurriTopic> children = topicRepository.findByParent(firstParent.getId());
        CurriTopic firstSubtopic = children.isEmpty() ? firstParent : children.get(0);

        // Redirect to the notes page of the first subtopic
        return "redirect:/read/notes/" + firstSubtopic.getSlug();
    }
}