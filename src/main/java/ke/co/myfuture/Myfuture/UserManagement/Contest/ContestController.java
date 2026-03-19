package ke.co.myfuture.Myfuture.UserManagement.Contest;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInviteeRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("contest")
public class ContestController {
    @Autowired
    ContestRepository repository;

    @Autowired
    IbukaStudentAccountRepository ibukaStudentAccountRepository;
    @Autowired
    CurriLevelRepository curriLevelRepository;
    @Autowired
    ContestInviteeRepository contestInviteeRepository;
    @Autowired
    ContestService contestService;

    @PostMapping("add")
    public ResponseEntity<?> newContest(@RequestBody ContestService.CreateContest createContest) {
        Optional<Contest> savedContest = contestService.createContest(createContest);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedContest);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @PutMapping("update")
//    public ResponseEntity<?> updateContest(@RequestBody Contest contest) {
//        Contest updatedContest = repository.save(contest);
//
//        UniversalResponse response = new UniversalResponse();
//        response.setStatus("Success");
//        response.setMessage("Updated Successfully");
//        response.setEntity(updatedContest);
//        response.setStatusCode(201);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
    @PutMapping("savescores")
    public ResponseEntity<?> saveScores(@RequestBody ContestService.ScoresParentHolder scoresParentHolder) {
        System.out.println("savescores");
        Boolean aBoolean = contestService.saveScores(scoresParentHolder);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(aBoolean);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id/{contestId}")
    public ResponseEntity<?> fetchContest(@PathVariable("contestId") Long contestId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Contest retrieved Successfully");
        response.setEntity(repository.findById(contestId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getall/after/id")
    public ResponseEntity<?> fetchAllAfterContest(@RequestParam("latestInviteId") Long latestInviteId, @RequestParam("studentId") Long studentId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Contest retrieved Successfully");
        response.setEntity(repository.contestsAfterInvite(latestInviteId, studentId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("search/invitees")
    public ResponseEntity<?> searchContestInvitees(@RequestParam("search") String search,
                                                   @RequestParam("classlevel") Long classlevel,
                                                   @RequestParam("studentId") Long studentId) {

        List<Long> classlevels = curriLevelRepository.classesAround(classlevel);
        System.out.println("Classes "+classlevels);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Invitees retrieved Successfully");
        ibukaStudentAccountRepository.updateRecentActivity(studentId);
        response.setEntity(ibukaStudentAccountRepository.contestInvitees(search, 30,  classlevels, studentId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("recent")
    public ResponseEntity<?> recentratings() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Retrieved successfully");
        response.setEntity(contestService.findAllContestSummaries());
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("invited")
    public ResponseEntity<?> fetchContestsInvited(@RequestParam("student") Long student) {
        Optional<IbukaStudentAccount> studentAccount = ibukaStudentAccountRepository.findById(student);
        return new ResponseEntity<>(contestInviteeRepository.findByStudentaccount(studentAccount.get()), HttpStatus.OK);
    }
}
