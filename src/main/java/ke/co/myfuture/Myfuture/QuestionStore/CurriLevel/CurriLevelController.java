package ke.co.myfuture.Myfuture.QuestionStore.CurriLevel;

import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevel;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevelRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
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

    @Autowired
    SubjectLevelRepository subjectLevelRepository;
    @PostMapping("add")
    public ResponseEntity<?> newCurriLevel(@RequestBody CurriLevel curriLevel) {
        // Save the new CurriLevel
        CurriLevel savedCurriLevel = classLevelRepository.save(curriLevel);

        // Create a response
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedCurriLevel);
        response.setStatusCode(HttpStatus.CREATED.value());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


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

    @PostMapping("{classlevelId}/subjects")
    public ResponseEntity<UniversalResponse<?>> addSubject(@RequestBody IdPostDTO subjectId,
                                                           @PathVariable("classlevelId") Long classlevelId) {
        System.out.println(subjectId);
        Optional<Subject> subject = subjectRepository.findById(subjectId.id);
        if (subject.isEmpty()) return null;

        Optional<CurriLevel> curriLevel = classLevelRepository.findById(classlevelId);
        if (curriLevel.isEmpty()) return null;

        SubjectLevel subjectLevel = new SubjectLevel();
        subjectLevel.setSubject(subject.get());
        subjectLevel.setCurriLevel(curriLevel.get());

        curriLevel.get().getSubjects().add(subject.get());

        subjectLevelRepository.save(subjectLevel);

        UniversalResponse universalResponse = new UniversalResponse();
        universalResponse.setEntity(classLevelRepository.findById(classlevelId));
        universalResponse.setMessage("Saved");
        universalResponse.setStatusCode(HttpStatus.FOUND.value());
        return ResponseEntity.ok().body(universalResponse);
    }


    @DeleteMapping("{classlevelId}/subjects/{subjectId}")
    public ResponseEntity<UniversalResponse<?>> deleteSubject(@PathVariable("classlevelId") Long classlevelId,
                                                              @PathVariable("subjectId") Long subjectId) {
        System.out.println(subjectId);
        Optional<SubjectLevel> subjectLevel = subjectLevelRepository.findByLevelAndSubject(classlevelId, subjectId);
        if (subjectLevel.isEmpty()) return null;

        subjectLevel.get().delete();

        subjectLevelRepository.save(subjectLevel.get());

        UniversalResponse universalResponse = new UniversalResponse();
        universalResponse.setEntity(classLevelRepository.findById(classlevelId));
        universalResponse.setMessage("Saved");
        universalResponse.setStatusCode(HttpStatus.FOUND.value());
        return ResponseEntity.ok().body(universalResponse);
    }

    @GetMapping("getbyid")
    public ResponseEntity<UniversalResponse<List<CurriLevel>>> getById(@RequestParam Long id) {
        Optional<CurriLevel> curriLevel = classLevelRepository.findById(id);

        UniversalResponse universalResponse = new UniversalResponse();
        universalResponse.setMessage("Retrieved");
        if (curriLevel.isPresent()) {
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

    @GetMapping("all/bycurriculum")
    public ResponseEntity<UniversalResponse<List<CurriLevel>>> getCurriLevelnsMinimal(@RequestParam("curriculum") Long curriculum) {
        List<CurriLevel> classLevelList = classLevelRepository.getAllByCurriculum(curriculum);

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
