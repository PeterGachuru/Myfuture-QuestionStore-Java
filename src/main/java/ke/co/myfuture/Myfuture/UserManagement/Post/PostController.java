package ke.co.myfuture.Myfuture.UserManagement.Post;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("post")
public class PostController {
    @Autowired
    PostRepository repository;

    @Autowired
    PostService postService;


    @PostMapping("add")
    public ResponseEntity<?> newPost(@RequestBody Post post) {
        if (post.id != null) return null;
        Post savedPost = repository.save(post);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedPost);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("addmany")
    public ResponseEntity<?> newPosts(@RequestBody PostService.PostsHolder postsHolder) {
        System.out.println("addmany");
        List<Post> savedPosts = postService.save(postsHolder);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedPosts);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updatePost(@RequestBody Post post) {
        Post updatedPost = repository.save(post);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedPost);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id/{postId}")
    public ResponseEntity<?> fetchPost(@PathVariable("postId") Long postId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Post retrieved Successfully");
        response.setEntity(repository.findById(postId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getall/after/id")
    public ResponseEntity<?> fetchAllAfterPost(@RequestParam("latestPostId") Long latestPostId) {
        System.out.println("latestPostId: "+latestPostId);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Post retrieved Successfully");
        response.setEntity(repository.postsAfter(latestPostId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("recent")
    public ResponseEntity<?> recentratings() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Retrieved successfully");
        response.setEntity(postService.findAllPostSummaries());
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}