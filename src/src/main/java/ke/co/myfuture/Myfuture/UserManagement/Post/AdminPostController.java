package ke.co.myfuture.Myfuture.UserManagement.Post;

import ke.co.myfuture.Myfuture.UserManagement.Post.Postatempt.PostattemptRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/posts")
public class AdminPostController {

    private final PostRepository postRepository;
    private final PostattemptRepository postattemptRepository;

    public AdminPostController(PostRepository postRepository, PostattemptRepository postattemptRepository) {
        this.postRepository = postRepository;
        this.postattemptRepository = postattemptRepository;
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

        return "admin/posts";
    }
}
