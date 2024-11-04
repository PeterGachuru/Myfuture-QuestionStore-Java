package ke.co.myfuture.Myfuture.UserManagement.QuizDone;

import ke.co.myfuture.Myfuture.UserManagement.Contest.Contest;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestService;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("quizdone")
public class QuizDoneController {
    @Autowired
    QuizDoneService quizDoneService;

    @PostMapping("add")
    public ResponseEntity<?> newContest(@RequestBody CreateQuizDone createQuizDone) {
        System.out.println(createQuizDone);
        Optional<QuizDone> savedQuizDone = quizDoneService.createQuiz(createQuizDone);
        if (savedQuizDone.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedQuizDone);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
