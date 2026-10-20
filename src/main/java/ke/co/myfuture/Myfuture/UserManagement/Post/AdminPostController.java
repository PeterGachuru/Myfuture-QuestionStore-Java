package ke.co.myfuture.Myfuture.UserManagement.Post;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestion;
import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestionRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import ke.co.myfuture.Myfuture.UserManagement.Post.Postatempt.PostattemptRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/posts")
public class AdminPostController {

    private final PostRepository postRepository;
    private final PostattemptRepository postattemptRepository;
    private final CurriQuestionRepository curriQuestionRepository;
    private final CurriLevelRepository curriLevelRepository;
    private final SubjectRepository subjectRepository;

    public AdminPostController(PostRepository postRepository, PostattemptRepository postattemptRepository,
                               CurriQuestionRepository curriQuestionRepository,
                               CurriLevelRepository curriLevelRepository,
                               SubjectRepository subjectRepository) {
        this.postRepository = postRepository;
        this.postattemptRepository = postattemptRepository;
        this.curriQuestionRepository = curriQuestionRepository;
        this.curriLevelRepository = curriLevelRepository;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping
    public String listPosts(Model model) {

        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 300));

        // Map postId -> number of attempts
        Map<Long, Long> attemptsMap = posts.stream()
                .collect(Collectors.toMap(
                        Post::getId,
                        p -> postattemptRepository.countByPost(p)
                ));

        model.addAttribute("posts", posts);
        model.addAttribute("attemptsMap", attemptsMap);

        List<Long> questionIds = posts.stream().map(Post::getQuestionid).distinct().toList();
        Map<Long, CurriQuestion> questionsById = questionIds.isEmpty() ? Map.of() :
                curriQuestionRepository.findAllById(questionIds).stream()
                        .collect(Collectors.toMap(CurriQuestion::getId, question -> question));
        Map<Long, String> classLevelLabels = curriLevelRepository.findAllById(questionsById.values().stream()
                        .filter(question -> question.getSubtopic() != null && question.getSubtopic().getCurriLevel() != null)
                        .map(question -> question.getSubtopic().getCurriLevel().getId()).distinct().toList())
                .stream().collect(Collectors.toMap(CurriLevel::getId,
                        level -> label(level.getName(), level.getId())));
        Map<Long, String> subjectLabels = subjectRepository.findAllById(questionsById.values().stream()
                        .filter(question -> question.getSubtopic() != null && question.getSubtopic().getSubject() != null)
                        .map(question -> question.getSubtopic().getSubject().getId()).distinct().toList())
                .stream().collect(Collectors.toMap(Subject::getId,
                        subject -> label(subject.getName(), subject.getId())));
        Map<Long, Map<String, String>> curriculumByQuestionId = questionsById.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    CurriQuestion question = entry.getValue();
                    Map<String, String> names = new HashMap<>();
                    if (question.getSubtopic() != null) {
                        names.put("topic", label(question.getSubtopic().getName(), question.getSubtopic().getId()));
                        if (question.getSubtopic().getCurriLevel() != null)
                            names.put("classLevel", classLevelLabels.getOrDefault(question.getSubtopic().getCurriLevel().getId(), "ID " + question.getSubtopic().getCurriLevel().getId()));
                        if (question.getSubtopic().getSubject() != null)
                            names.put("subject", subjectLabels.getOrDefault(question.getSubtopic().getSubject().getId(), "ID " + question.getSubtopic().getSubject().getId()));
                    }
                    return names;
                }));
        model.addAttribute("curriculumByQuestionId", curriculumByQuestionId);

        return "admin/posts";
    }

    private static String label(String name, Long id) {
        return name == null || name.isBlank() ? "ID " + id : name + " (ID " + id + ")";
    }
}
