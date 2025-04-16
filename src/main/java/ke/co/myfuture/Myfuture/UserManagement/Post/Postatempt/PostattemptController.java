package ke.co.myfuture.Myfuture.UserManagement.Post.Postatempt;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.Post.PostRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("post/attempts")
public class PostattemptController {
    @Autowired
    PostattemptRepository repository;
    @Autowired
    IbukaStudentAccountRepository ibukaStudentAccountRepository;
    @Autowired
    PostRepository postRepository;


    @PostMapping("add")
    public ResponseEntity<?> newPostattempt(@RequestBody PostattemptRequest postattemptRequest) {
        Postattempt postattempt = new Postattempt();
        postattempt.scored = postattemptRequest.scored;
        postattempt.selectedChoice = postattemptRequest.selectedChoice;
        postattempt.studentaccount = ibukaStudentAccountRepository.findById(postattemptRequest.studentId).get();
        postattempt.post = postRepository.findById(postattemptRequest.postId).get();
        Postattempt savedPostattempt = repository.save(postattempt);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedPostattempt);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("addall")
    public ResponseEntity<?> newPostattempt(@RequestBody ArrayPostattemptRequest arrayPostattemptRequest) {

        List<Map<String, Long>> saved = new ArrayList<>();
        Optional<IbukaStudentAccount> ibukaStudentAccount;

        for (PostattemptRequest postattemptRequest : arrayPostattemptRequest.attempts) {
            System.out.println(postattemptRequest);
            Postattempt postattempt = new Postattempt();
            postattempt.scored = postattemptRequest.scored;
            postattempt.selectedChoice = postattemptRequest.selectedChoice;

            ibukaStudentAccount = ibukaStudentAccountRepository.findById(postattemptRequest.studentId);
            if (ibukaStudentAccount.isEmpty())
                continue;

            postattempt.studentaccount = ibukaStudentAccount.get();
            postattempt.post = postRepository.findById(postattemptRequest.postId).get();
            Postattempt savedPostattempt = repository.save(postattempt);

            Map<String, Long> result = new HashMap<>();
            result.put("studentId", savedPostattempt.studentaccount.id); // assuming there's a field `id`
            result.put("postId", savedPostattempt.post.id);              // assuming there's a field `id`
            result.put("attemptId", savedPostattempt.id);              // assuming there's a field `id`
            saved.add(result);
        }

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(saved);
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
