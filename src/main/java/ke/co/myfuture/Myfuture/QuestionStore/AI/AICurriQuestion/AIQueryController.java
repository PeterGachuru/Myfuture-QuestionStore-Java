package ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriQuestion;

import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicService;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("ai")
public class AIQueryController {
    @Autowired
    ChatGPTQuestionsService chatGPTQuestionsService;

    @Autowired
    CurriTopicRepository curriTopicRepository;

    @Autowired
    CurriTopicService curriTopicService;


    @GetMapping("fill-question")
    public ResponseEntity<?> fillQuestions() {
        chatGPTQuestionsService.queryCurriQuestionsForAllSubtopics();
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Generated Successfully");
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("fill-content")
    public ResponseEntity<?> fillContent() {
        chatGPTQuestionsService.queryContentForAllSubtopics();
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Generated Successfully");
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("generate/questions/for-subtopic")
    public ResponseEntity<?> generateQuestionsForSubtopic(@RequestBody AIPromptRequest request) {
        chatGPTQuestionsService.generateForSubtopic(request.model, curriTopicRepository.findById(request.subtopicId).get());
        curriTopicService.updateCurriTopicStats();
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Generated Successfully");
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("generate/questions/for-topic")
    public ResponseEntity<?> generateQuestionsForTopic(@RequestBody AIPromptRequest request) {
        List<CurriTopic> curriTopicList  =  curriTopicRepository.findByParent(request.topicId);
        for (CurriTopic curriTopic: curriTopicList) {
            chatGPTQuestionsService.generateForSubtopic(request.model, curriTopic);
        }
        curriTopicService.updateCurriTopicStats();
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Generated Successfully");
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("approve/questions/for-topic")
    public ResponseEntity<?> approveQuestionsForTopic(@RequestBody AIPromptRequest request) {
        chatGPTQuestionsService.approveQuestionsWithAIByTopic(request.getTopicId());
        curriTopicService.updateCurriTopicStats();
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Approved Successfully");
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("generate/questions/for-subject")
    public ResponseEntity<?> generateQuestionsForSubject(@RequestBody AIPromptRequest request) {
        List<CurriTopic> topicList  =  curriTopicRepository.findBySubjectAndClass(request.subjectId,
                request.getLevelId());
        for (CurriTopic topic: topicList) {
            List<CurriTopic> subtopicList  =  curriTopicRepository.findByParent(topic.getId());
            for (CurriTopic subtopic: subtopicList) {
                chatGPTQuestionsService.generateForSubtopic(request.model,  subtopic);
            }
        }
        curriTopicService.updateCurriTopicStats();

        chatGPTQuestionsService.queryContentForAllSubtopics();
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Generated Successfully");
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}