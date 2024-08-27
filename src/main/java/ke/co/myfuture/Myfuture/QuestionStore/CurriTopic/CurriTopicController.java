package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("topic")
public class CurriTopicController {
    @Autowired
    CurriTopicRepository repository;

    @PostMapping("add/")
    public ResponseEntity<?> newCurriTopic(@RequestBody CurriTopic topic) {
//        topic.setSubtopicContent(new SubtopicContent());
        CurriTopic savedCurriTopic = repository.save(topic);
        System.out.println(savedCurriTopic);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedCurriTopic);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateCurriTopic(@RequestBody CurriTopic topic) {
        Optional<CurriTopic> dbCurriTopic = repository.findById(topic.id);
        if (dbCurriTopic.isPresent()) {
            CurriTopic curriTopic = dbCurriTopic.get();
            curriTopic.setContent(topic.getContent());
//            curriTopic.setSubtopicContent(topic.getSubtopicContent());
            curriTopic.setName(topic.getName());
            CurriTopic savedSubtopic = repository.save(curriTopic);
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Success");
            response.setMessage("Updated Successfully");
            response.setEntity(savedSubtopic);
            response.setStatusCode(201);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Could not update");
        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
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

    @GetMapping("withcontent")
    public ResponseEntity<?> fetchCurriTopic() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriTopic retrieved Successfully");
        response.setEntity(repository.findParentsWithContent());
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

    @GetMapping("all/withunapprovedquestions")
    public ResponseEntity<UniversalResponse<List<CurriLevel>>> getCurriLevelnsMinimalWithUnapprovedQuestions(@RequestParam(value = "parent", required = false) Long parent, @RequestParam("subject") Long subject, @RequestParam("class") Long classLevel) {
        List<CurriTopic> classLevelList;
        if (parent == null){
            classLevelList = repository.getAllWithUnapprovedQuestions(subject, classLevel);
        }else {
            classLevelList = repository.getAllWithUnapprovedQuestions(parent, subject, classLevel);
        }

        UniversalResponse universalResponse = new UniversalResponse();
        universalResponse.setEntity(classLevelList);
        universalResponse.setMessage("Retrieved");
        universalResponse.setStatusCode(HttpStatus.FOUND.value());
        return ResponseEntity.ok().body(universalResponse);
    }
}
