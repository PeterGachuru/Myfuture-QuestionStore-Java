package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

import ke.co.myfuture.Myfuture.QuestionStore.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("topic")
public class CurriTopicController {
    @Autowired
    CurriTopicRepository repository;

    @PostMapping("add/")
    public ResponseEntity<?> newCurriTopic(@RequestBody CurriTopic topic) {
        CurriTopic savedCurriTopic = repository.save(topic);
        System.out.println(savedCurriTopic);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedCurriTopic);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/")
    public ResponseEntity<?> updateCurriTopic(@RequestBody CurriTopic topic) {
        CurriTopic updatedCurriTopic = repository.save(topic);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedCurriTopic);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchCurriTopic(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriTopic retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("get/by/subjectandclass")
    public ResponseEntity<?> fetchCurriTopic(@RequestParam("subject") Long subject, @RequestParam("class") Long classLevel) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriTopic retrieved Successfully");
        response.setEntity(repository.findBySubjectAndClass(subject, classLevel));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/parent")
    public ResponseEntity<?> fetchCurriTopicByParent(@RequestParam("parentId") Long parentId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriTopic retrieved Successfully");
        response.setEntity(repository.findByParent(parentId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
