package ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriQuestion;

import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicService;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    @PostMapping("fillallsubtopics")
    public ResponseEntity<?> fillAllSubtopics(@RequestParam String model) {
        chatGPTQuestionsService.fillAllSubtopicsWithQuestionToMeetMinimum(model);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Started filling for all Successfully");
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

    @PostMapping(value = "approve/questions/for-topic", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter approveQuestionsForTopic(@RequestBody AIPromptRequest request) {
        SseEmitter emitter = new SseEmitter();
        UniversalResponse response = new UniversalResponse();

        new Thread(() -> {
            try {
                // Step 1: Start processing and send an initial update
                response.setStatus("Processing");
                response.setMessage("Started approving questions...");
                response.setStatusCode(100);
                emitter.send(response);
                System.out.println("Step 1: " + response.getMessage());

                // Step 2: Approve questions with AI by topic
                System.out.println("Step 2: Approving questions for topic ID: " + request.getTopicId());
                chatGPTQuestionsService.approveQuestionsWithAIByTopic(request.getTopicId(), emitter);
                response.setMessage("Questions approved.");
                response.setEntity(new QuestionApprovalsResponse());
                emitter.send(response);

                // Step 3: Update curriculum topic statistics
                System.out.println("Step 3: Updating curriculum topic statistics...");
                curriTopicService.updateCurriTopicStats();
                response.setMessage("Topic statistics updated.");
                response.setEntity(new QuestionApprovalsResponse());
                System.out.println("Step 3 Completed: " + response.getMessage());
                emitter.send(response);

                // Step 4: Final success response
                response.setStatus("Success");
                response.setEntity(new QuestionApprovalsResponse());
                response.setMessage("Approved Successfully");
                response.setStatusCode(200);
                System.out.println("Step 4: " + response.getMessage());
                emitter.send(response);

                // Step 5: Complete the stream
                System.out.println("Approval process completed successfully.");
                emitter.complete();
            } catch (Exception e) {
                response.setStatus("Error");
                response.setMessage("An error occurred: " + e.getMessage());
                response.setStatusCode(500);
                System.out.println("Error: " + response.getMessage());
                try {
                    emitter.send(response);
                } catch (Exception ignored) {
                }
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
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