package ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel;


import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("subjectlevel")
public class SubjectLevelController {
    @Autowired
    SubjectLevelRepository subjectLevelRepository;

    @GetMapping("bycurriculum")
    public ResponseEntity<?> fetchByCurriculum(@RequestParam("curriculum") Long curriculum) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriTopic retrieved Successfully");
        response.setEntity(subjectLevelRepository.findByCurriculum(curriculum));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
