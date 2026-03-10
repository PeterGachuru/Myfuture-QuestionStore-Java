package ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.Curriculum;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.CurriculumRepository;
import ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.Cronjobs;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("student")
public class StudentAccountController {
    @Autowired
    IbukaStudentAccountRepository repository;
    @Autowired
    Cronjobs cronjobs;
    @Autowired
    CurriculumRepository curriculumRepository;
    @Autowired
    CurriLevelRepository curriLevelRepository;
    @Autowired
    IbukaStudentAccountService ibukaStudentAccountService;

    @PostMapping("add")
    public ResponseEntity<?> newStudentAccount(@RequestBody IbukaStudentAccount student) {
        if (student.id != null) return null;

        IbukaStudentAccount savedStudentAccount = repository.save(student);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedStudentAccount);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateStudentAccount(@RequestBody IbukaStudentAccount student) {
        if (student.id == null) return null;

        Optional<IbukaStudentAccount> studentAccountFromDb = repository.findById(student.id);
        if (studentAccountFromDb.isEmpty()) return null;
        studentAccountFromDb.get().update(student);
        Long initialScore = studentAccountFromDb.get().totalScore;
        IbukaStudentAccount updatedStudentAccount = repository.save(studentAccountFromDb.get());

        if (updatedStudentAccount.totalScore > initialScore) {
            System.out.println("Score is higher from initial");
            cronjobs.analyzeScoresDeep();
        }
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedStudentAccount);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id/{studentId}")
    public ResponseEntity<?> fetchStudentAccount(@PathVariable("studentId") Long studentId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("StudentAccount retrieved Successfully");
        response.setEntity(repository.findById(studentId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/parent")
    public ResponseEntity<?> fetchAll(@RequestParam("parentId") Long parentId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("StudentAccount retrieved Successfully");
        response.setEntity(repository.findByParent(parentId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getdeep/by/parent")
    public ResponseEntity<?> fetchAllDeep(@RequestParam("parentId") Long parentId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("StudentAccount retrieved Successfully");
        response.setEntity(ibukaStudentAccountService.findByParent(parentId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("from/ids")
    public ResponseEntity<?> fetchAll(@RequestBody Ids ids) {
//        System.out.println(" @PostMapping(\"from/ids\")");
//        System.out.println(ids);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("StudentAccount retrieved Successfully");
        response.setEntity(repository.findAllById(ids.ids));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("contest/Invitees")
    public ResponseEntity<?> forContest(@RequestParam String search, @RequestParam Long studentId,
                                        @RequestParam Long classlevel,
                                        @RequestParam Integer count) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("StudentAccount retrieved Successfully");
        response.setEntity(repository.contestInvitees(search, count, classlevel, studentId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("recent")
    public ResponseEntity<?> recentratings() {
        // Load and cache all subjects
        Map<Long, Curriculum> curriculumMap = curriculumRepository.findAll()
                .stream()
                .collect(Collectors.toMap(curriculum -> curriculum.getId(), subject -> subject));

        // Load and cache all curriLevels
        Map<Long, CurriLevel> curriLevelMap = curriLevelRepository.findAll()
                .stream()
                .collect(Collectors.toMap(curriLevel -> curriLevel.getId(), curriLevel -> curriLevel));

        List<IbukaStudentAccount> ibukaStudentAccounts = repository.findTop200ByOrderByCreatedAtDesc();
        List<IbukaStudentAccount> dtos = ibukaStudentAccounts.stream()
                .map(account -> {
                    Curriculum curriculum = curriculumMap.getOrDefault(account.curriculum, null);
                    CurriLevel curriLevel = (account.classlevel != null) ? curriLevelMap.getOrDefault(account.classlevel, null) : null;
                    account.setCurriLevel(curriLevel);
                    account.setCurriculumObject(curriculum);
                    return account;
                })
                .collect(Collectors.toList());

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Retrieved successfully");
        response.setEntity(dtos);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Data
    static class Ids {
        List<Long> ids;
    }
}
