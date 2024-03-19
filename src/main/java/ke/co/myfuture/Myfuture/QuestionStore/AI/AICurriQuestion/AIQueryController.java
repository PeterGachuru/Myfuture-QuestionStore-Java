package ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriQuestion;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("chatgpt")
public class AIQueryController {
    @Autowired
    ChatGPTQuestionsService chatGPTQuestionsService;
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

}
