package ke.co.myfuture.Myfuture.UserManagement.QuizDone;

import ke.co.myfuture.Myfuture.UserManagement.Contest.Contest;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestService;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("quizdone")
public class QuizDoneController {
    @Autowired
    QuizDoneService quizDoneService;
    @Autowired
    QuizDoneRepository quizDoneRepository;
    @Autowired
    IbukaStudentAccountRepository ibukaStudentAccountRepository;

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

    @GetMapping("recent")
    public ResponseEntity<?> recentratings() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Retrieved successfully");
        response.setEntity(quizDoneService.findAll());
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("recentForStudent")
    public ResponseEntity<?> recentQuizesForStudent(@RequestParam Long studentId) {
        Optional<IbukaStudentAccount> ibukaStudentAccount = ibukaStudentAccountRepository.findById(studentId);
        return new ResponseEntity<>(quizDoneRepository.findByStudentOrderByCreatedAtDesc(ibukaStudentAccount.get()), HttpStatus.OK);
    }
}
