package ke.co.myfuture.Myfuture.QuestionStore.CurriLevel;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("classlevel")
public class CurriLevelController {
    @Autowired
    CurriLevelRepository classLevelRepository;
    @Autowired
    SubjectRepository subjectRepository;

    @GetMapping("all")
    public ResponseEntity<UniversalResponse<List<CurriLevel>>> getCurriLevels() {
        List<CurriLevel> classLevelList = classLevelRepository.findAll();
        for (CurriLevel classLevel: classLevelList) {
            classLevel.setSubjects(subjectRepository.subjectsByClassLevel(classLevel.id));
        }

        UniversalResponse universalResponse = new UniversalResponse();
        universalResponse.setEntity(classLevelList);
        universalResponse.setMessage("Retrieved");
        universalResponse.setStatusCode(HttpStatus.FOUND.value());
        return ResponseEntity.ok().body(universalResponse);
    }
    @GetMapping("getbyid")
    public ResponseEntity<UniversalResponse<List<CurriLevel>>> getById(@RequestParam Long id) {
        Optional<CurriLevel> curriLevel = classLevelRepository.findById(id);

        UniversalResponse universalResponse = new UniversalResponse();
        universalResponse.setMessage("Retrieved");
        if (curriLevel.isPresent()){
            CurriLevel classLevel = curriLevel.get();
            classLevel.setSubjects(subjectRepository.subjectsByClassLevel(classLevel.id));
            universalResponse.setEntity(classLevel);
        }

        universalResponse.setStatusCode(HttpStatus.FOUND.value());
        return ResponseEntity.ok().body(universalResponse);
    }
    @GetMapping("all/minimal")
    public ResponseEntity<UniversalResponse<List<CurriLevel>>> getCurriLevelnsMinimal() {
        List<CurriLevel> classLevelList = classLevelRepository.findAll();

        UniversalResponse universalResponse = new UniversalResponse();
        universalResponse.setEntity(classLevelList);
        universalResponse.setMessage("Retrieved");
        universalResponse.setStatusCode(HttpStatus.FOUND.value());
        return ResponseEntity.ok().body(universalResponse);
    }

    @GetMapping("all/withunapprovedquestions")
    public ResponseEntity<UniversalResponse<List<CurriLevel>>> getCurriLevelnsMinimalWithUnapprovedQuestions(@RequestParam("curriculum") Long curriculum) {
        List<CurriLevel> classLevelList = classLevelRepository.getAllWithUnapprovedQuestions(curriculum);

        UniversalResponse universalResponse = new UniversalResponse();
        universalResponse.setEntity(classLevelList);
        universalResponse.setMessage("Retrieved");
        universalResponse.setStatusCode(HttpStatus.FOUND.value());
        return ResponseEntity.ok().body(universalResponse);
    }
}
