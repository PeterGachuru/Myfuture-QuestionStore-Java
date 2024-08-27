package ke.co.myfuture.Myfuture.QuestionStore.Curriculum;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("curriculums")
public class CurriculumController {
    @Autowired
    CurriculumRepository curriculumRepository;
    @Autowired
    SubjectRepository subjectRepository;

    @GetMapping("all")
    public ResponseEntity<UniversalResponse<List<Curriculum>>> getCurriculumns() {
        List<Curriculum> curriculumList = curriculumRepository.getAllCurriculums();
        for (Curriculum curriculum: curriculumList) {
            for (CurriLevel curriLevel: curriculum.curriLevels) {
                curriLevel.setSubjects(subjectRepository.subjectsByClassLevel(curriLevel.id));
            }
        }

        UniversalResponse universalResponse = new UniversalResponse();
        universalResponse.setEntity(curriculumList);
        universalResponse.setMessage("Retrieved");
        universalResponse.setStatusCode(HttpStatus.FOUND.value());
        return ResponseEntity.ok().body(universalResponse);
    }
    @GetMapping("all/minimal")
    public ResponseEntity<UniversalResponse<List<Curriculum>>> getCurriculumnsMinimal() {
        List<Curriculum> curriculumList = curriculumRepository.getAllCurriculums();

        UniversalResponse universalResponse = new UniversalResponse();
        universalResponse.setEntity(curriculumList);
        universalResponse.setMessage("Retrieved");
        universalResponse.setStatusCode(HttpStatus.FOUND.value());
        return ResponseEntity.ok().body(universalResponse);
    }

    @GetMapping("all/withunapprovedquestions")
    public ResponseEntity<UniversalResponse<List<Curriculum>>> getCurriculumnsWithUnapprovedQuestions() {
        List<Curriculum> curriculumList = curriculumRepository.getAllCurriculumsWithUnapprovedQuestions();

        UniversalResponse universalResponse = new UniversalResponse();
        universalResponse.setEntity(curriculumList);
        universalResponse.setMessage("Retrieved");
        universalResponse.setStatusCode(HttpStatus.FOUND.value());
        return ResponseEntity.ok().body(universalResponse);
    }
}