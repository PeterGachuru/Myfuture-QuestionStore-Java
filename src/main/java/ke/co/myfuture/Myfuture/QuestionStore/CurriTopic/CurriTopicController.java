package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("topic")
public class CurriTopicController {
    @Autowired
    CurriTopicRepository curriTopicRepository;
    @Autowired
    CurriLevelRepository curriLevelRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    CurriTopicService curriTopicService;

    @PostMapping("add")
    public ResponseEntity<?> newCurriTopic(@RequestBody TopicDto topicDto) {
        CurriTopic topic = new CurriTopic();
        topic.setCurriLevel(curriLevelRepository.findById(topicDto.getCurriLevel()).get());
        topic.setSubject(subjectRepository.findById(topicDto.getSubject()).get());
        CurriTopic parent = null;
        if (topicDto.getParent() != null) {
            parent = curriTopicRepository.findById(topicDto.getParent()).get();
            topic.setParent(parent);
        }
        topic.setName(topicDto.getName());
        topic.setNumbering(topicDto.getOrder());
        CurriTopic savedCurriTopic = curriTopicRepository.save(topic);
        if (parent != null) {
            parent.setIsParent(true);
            curriTopicRepository.save(parent);
        }
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
        Optional<CurriTopic> dbCurriTopic = curriTopicRepository.findById(topic.id);
        if (dbCurriTopic.isPresent()) {
            CurriTopic curriTopic = dbCurriTopic.get();
            curriTopic.update(topic);
            CurriTopic savedSubtopic = curriTopicRepository.save(curriTopic);
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

    @PutMapping("reorder")
    public ResponseEntity<?> updateTopics(@RequestBody Map<String, List<Map<String, Long>>> payload) {
        List<Map<String, Long>> topics = payload.get("topics");

        if (topics == null || topics.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload: topics list is missing or empty");
        }

        List<CurriTopic> updatedTopics = new ArrayList<>();

        for (Map<String, Long> topicData : topics) {
            Long topicId = topicData.get("id");
            Integer numbering = topicData.get("numbering").intValue();

            if (topicId == null || numbering == null) {
                continue; // Skip invalid entries
            }

            Optional<CurriTopic> dbCurriTopic = curriTopicRepository.findById(topicId);
            if (dbCurriTopic.isPresent()) {
                CurriTopic curriTopic = dbCurriTopic.get();
                curriTopic.setNumbering(numbering);
                CurriTopic savedTopic = curriTopicRepository.save(curriTopic);
                updatedTopics.add(savedTopic);
            }
        }

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Topics updated successfully");
        response.setEntity(updatedTopics);
        response.setStatusCode(201);

        return ResponseEntity.ok(response);
    }

    @PutMapping("{topicId}/update-name")
    public ResponseEntity<?> rename(@RequestBody TopicDto topicDto,
                                    @PathVariable("topicId") Long topicId) {
        Optional<CurriTopic> dbCurriTopic = curriTopicRepository.findById(topicId);
        if (dbCurriTopic.isPresent()) {
            CurriTopic curriTopic = dbCurriTopic.get();
            curriTopic.setName(topicDto.getName());
            CurriTopic savedSubtopic = curriTopicRepository.save(curriTopic);
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

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCurriTopic(@PathVariable Long id) {
        Optional<CurriTopic> dbCurriTopic = curriTopicRepository.findById(id);
        if (dbCurriTopic.isPresent()) {
            curriTopicService.delete(dbCurriTopic.get());
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Success");
            response.setMessage("Deleted Successfully");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Failure");
        response.setMessage("Topic not found");
        response.setStatusCode(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchCurriTopic(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriTopic retrieved Successfully");
        response.setEntity(curriTopicRepository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/subjectandclass")
    public ResponseEntity<?> fetchCurriTopic(@RequestParam("subject") Long subject, @RequestParam("class") Long classLevel) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriTopic retrieved Successfully");
        response.setEntity(curriTopicRepository.findBySubjectAndClass(subject, classLevel));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("get/by/curriculum")
    public ResponseEntity<?> fetchCurriTopicByCurriculum(@RequestParam("curriculum") Long curriculum) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriTopic retrieved Successfully");
        response.setEntity(curriTopicRepository.findAllTopicsByCurriculum( curriculum));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("get/by/classlevel")
    public ResponseEntity<?> fetchCurriTopicByClasslevel(@RequestParam("classlevel") Long classlevel) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriTopic retrieved Successfully");
        response.setEntity(curriTopicRepository.findAllTopicsByClassLevel(classlevel));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("withcontent")
    public ResponseEntity<?> fetchCurriTopic() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriTopic retrieved Successfully");
        response.setEntity(curriTopicRepository.findParentsWithContent());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("get/by/parent")
    public ResponseEntity<?> fetchCurriTopicByParent(@RequestParam("parentId") Long parentId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriTopic retrieved Successfully");
        response.setEntity(curriTopicRepository.findByParent(parentId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("all/withunapprovedquestions")
    public ResponseEntity<UniversalResponse<List<CurriLevel>>> getCurriLevelnsMinimalWithUnapprovedQuestions(@RequestParam(value = "parent", required = false) Long parent, @RequestParam("subject") Long subject, @RequestParam("class") Long classLevel) {
        List<CurriTopic> classLevelList;
        if (parent == null){
            classLevelList = curriTopicRepository.getAllWithUnapprovedQuestions(subject, classLevel);
        }else {
            classLevelList = curriTopicRepository.getAllWithUnapprovedQuestions(parent, subject, classLevel);
        }

        UniversalResponse universalResponse = new UniversalResponse();
        universalResponse.setEntity(classLevelList);
        universalResponse.setMessage("Retrieved");
        universalResponse.setStatusCode(HttpStatus.FOUND.value());
        return ResponseEntity.ok().body(universalResponse);
    }
}
