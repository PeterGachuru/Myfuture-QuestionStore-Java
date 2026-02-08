package ke.co.myfuture.Myfuture.QuestionStore.CurriNotes;

import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/read/topic")
public class CurriNotesReadController {
    private final CurriTopicRepository topicRepository;
    private final CurriNotesRepository notesRepository;

    public CurriNotesReadController(
            CurriTopicRepository topicRepository,
            CurriNotesRepository notesRepository) {
        this.topicRepository = topicRepository;
        this.notesRepository = notesRepository;
    }

    @GetMapping("/{topicId}/notes")
    public String readNotes(@PathVariable Long topicId, Model model) {
        CurriTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Subtopic not found"));

        CurriNotes notes = notesRepository
                .findBySubtopicIdAndDeletedFlagFalse(topicId)
                .orElse(null);

        model.addAttribute("topic", topic);
        model.addAttribute("notes", notes);

        return "read/notes/profile";
    }
}
