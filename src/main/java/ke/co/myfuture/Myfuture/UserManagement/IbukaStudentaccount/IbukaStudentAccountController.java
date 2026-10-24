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
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("student")
public class IbukaStudentAccountController {
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
    @Autowired
    StudentScoreEventService studentScoreEventService;

    @PostMapping("add")
    public ResponseEntity<?> newStudentAccount(@RequestBody IbukaStudentAccount student) {
        System.out.println("=================Creating student===================");
        System.out.println(student);
        if (student.id != null) return null;

        IbukaStudentAccount savedStudentAccount = repository.save(student);
        try {
            savedStudentAccount = ibukaStudentAccountService.createSenderIdIfMissing(savedStudentAccount);
        } catch (Exception e) {
            System.err.println("Failed to create sender ID for new student ID=" + savedStudentAccount.getId()
                    + " | Reason: " + e.getMessage());
        }
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

        System.out.println("=====================================================");
        System.out.println("Received student: "+student);
        Optional<IbukaStudentAccount> studentAccountFromDb = repository.findById(student.id);
        if (studentAccountFromDb.isEmpty()) return null;
        if (student.unsyncedScore != null && student.unsyncedScore > 0
                && LocalDate.now(ZoneId.of("Africa/Nairobi")).isAfter(LocalDate.of(2026, 12, 15))) {
            return ResponseEntity.status(HttpStatus.GONE).body(Map.of(
                    "status", "Error",
                    "statusCode", 410,
                    "message", "Legacy additive score sync expired. Update the app to sync scores."
            ));
        }
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

    /** Profile-only endpoint for current clients; legacy score deltas cannot be applied here. */
    @PutMapping("update-v2")
    public ResponseEntity<?> updateStudentAccountV2(@RequestBody IbukaStudentAccount student) {
        if (student.id == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "Error", "statusCode", 400));
        }
        Optional<IbukaStudentAccount> existing = repository.findById(student.id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        IbukaStudentAccount current = existing.get();
        if (!ownsStudent(current, student)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "Error", "statusCode", 403));
        }
        current.updateProfile(student);
        IbukaStudentAccount saved = repository.save(current);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(saved);
        response.setStatusCode(200);
        return ResponseEntity.ok(response);
    }

    @PostMapping("score-events")
    public ResponseEntity<?> recordScoreEvent(@RequestBody StudentScoreEventRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())
                    ? null : authentication.getName();
            StudentScoreEventService.Result result = studentScoreEventService.record(request, email);
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Success");
            response.setMessage(result.applied() ? "Score recorded" : "Score event already recorded");
            response.setEntity(Map.of("applied", result.applied(), "totalScore", result.totalScore()));
            response.setStatusCode(200);
            return ResponseEntity.ok(response);
        } catch (SecurityException exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "Error", "statusCode", 403, "message", exception.getMessage()));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "Error", "statusCode", 400, "message", exception.getMessage()));
        } catch (StudentScoreEventService.StudentNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "Error", "statusCode", 404, "message", exception.getMessage()));
        }
    }

    private boolean ownsStudent(IbukaStudentAccount current, IbukaStudentAccount request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean accountOwns = authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())
                && current.parentUsername != null
                && current.parentUsername.equalsIgnoreCase(authentication.getName());
        boolean installationOwns = request.installId != null && request.inid != null
                && request.installId.equals(current.installId) && request.inid.equals(current.inid)
                && request.parent != null && request.parent.equals(current.parent);
        return accountOwns || installationOwns;
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

//    @PostMapping("contest/Invitees")
//    public ResponseEntity<?> forContest(@RequestParam String search, @RequestParam Long studentId,
//                                        @RequestParam Long classlevel,
//                                        @RequestParam Integer count) {
//        UniversalResponse response = new UniversalResponse();
//        response.setStatus("Success");
//        response.setMessage("StudentAccount retrieved Successfully");
//        response.setEntity(repository.contestInvitees(search, count, classlevel, studentId));
//        response.setStatusCode(200);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

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
