package ke.co.myfuture.Myfuture.QuestionStore.Subject;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevelRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("subjects")
public class SubjectController {
    @Autowired
    SubjectRepository subjectRepository;

    @GetMapping("all/withunapprovedquestions")
    public ResponseEntity<UniversalResponse<List<CurriLevel>>> getCurriLevelnsMinimalWithUnapprovedQuestions(@RequestParam("level") Long level) {
        List<Subject> classLevelList = subjectRepository.getAllWithUnapprovedQuestions(level);

        UniversalResponse universalResponse = new UniversalResponse();
        universalResponse.setEntity(classLevelList);
        universalResponse.setMessage("Retrieved");
        universalResponse.setStatusCode(HttpStatus.FOUND.value());
        return ResponseEntity.ok().body(universalResponse);
    }
    @GetMapping("all")
    public ResponseEntity<UniversalResponse<List<CurriLevel>>> getAll() {
        List<Subject> subjects = subjectRepository.findAll();

        UniversalResponse universalResponse = new UniversalResponse();
        universalResponse.setEntity(subjects);
        universalResponse.setMessage("Retrieved");
        universalResponse.setStatusCode(HttpStatus.FOUND.value());
        return ResponseEntity.ok().body(universalResponse);
    }
    @PostMapping("add")
    public ResponseEntity<UniversalResponse<?>> add(@RequestBody Subject subject) {
        System.out.println("Subject @PostMapping(\"add\")");
        System.out.println(subject);
        if (subject.id != null) return null;

        Subject saved = subjectRepository.save(subject);

        UniversalResponse universalResponse = new UniversalResponse();
        universalResponse.setEntity(saved);
        universalResponse.setMessage("Saved");
        universalResponse.setStatusCode(HttpStatus.FOUND.value());
        return ResponseEntity.ok().body(universalResponse);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchCurriTopic(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriTopic retrieved Successfully");
        response.setEntity(subjectRepository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
