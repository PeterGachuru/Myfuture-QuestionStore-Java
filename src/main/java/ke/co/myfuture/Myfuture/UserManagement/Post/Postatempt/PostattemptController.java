package ke.co.myfuture.Myfuture.UserManagement.Post.Postatempt;

import ke.co.myfuture.Myfuture.UserManagement.Post.PostRepository;
import ke.co.myfuture.Myfuture.UserManagement.Studentaccount.StudentAccountRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public class PostattemptController {
    @Autowired
    PostattemptRepository repository;
    @Autowired
    StudentAccountRepository studentAccountRepository;
    @Autowired
    PostRepository postRepository;


    @PostMapping("add/")
    public ResponseEntity<?> newPostattempt(@RequestBody PostattemptRequest postattemptRequest) {
        Postattempt postattempt = new Postattempt();
        postattempt.scored = postattemptRequest.scored;
        postattempt.selectedChoice = postattemptRequest.selectedChoice;
        postattempt.studentaccount = studentAccountRepository.findById(postattemptRequest.studentId).get();
        postattempt.post = postRepository.findById(postattemptRequest.postId).get();
        Postattempt savedPostattempt = repository.save(postattempt);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedPostattempt);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/")
    public ResponseEntity<?> updatePostattempt(@RequestBody Postattempt postattempt) {
        Postattempt updatedPostattempt = repository.save(postattempt);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedPostattempt);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id/{postattemptId}")
    public ResponseEntity<?> fetchPostattempt(@PathVariable("postattemptId") Long postattemptId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Postattempt retrieved Successfully");
        response.setEntity(repository.findById(postattemptId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getall/after/id/{postattemptId}")
    public ResponseEntity<?> fetchAllAfterPostattempt(@PathVariable("postattemptId") Long postattemptId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Postattempt retrieved Successfully");
        response.setEntity(repository.postattemptsAfter(postattemptId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
