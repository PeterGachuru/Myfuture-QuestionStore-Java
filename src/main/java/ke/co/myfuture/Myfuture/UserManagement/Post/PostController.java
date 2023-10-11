package ke.co.myfuture.Myfuture.UserManagement.Post;

import ke.co.myfuture.Myfuture.QuestionStore.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("post")
public class PostController {
    @Autowired
    PostRepository repository;


    @PostMapping("add/")
    public ResponseEntity<?> newPost(@RequestBody Post post) {
        Post savedPost = repository.save(post);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedPost);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/")
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

    @GetMapping("getall/after/id/{postId}")
    public ResponseEntity<?> fetchAllAfterPost(@PathVariable("postId") Long postId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Post retrieved Successfully");
        response.setEntity(repository.postsAfter(postId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}