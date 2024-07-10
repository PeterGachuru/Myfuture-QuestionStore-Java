package ke.co.myfuture.Myfuture.UserManagement.Contest;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import ke.co.myfuture.Myfuture.UserManagement.Studentaccount.StudentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("contest")
public class ContestController {
    @Autowired
    ContestRepository repository;

    @Autowired
    StudentAccountRepository studentAccountRepository;
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

    @PutMapping("update/")
    public ResponseEntity<?> updateContest(@RequestBody Contest contest) {
        Contest updatedContest = repository.save(contest);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedContest);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
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
    public ResponseEntity<?> fetchAllAfterContest(@RequestParam("latestContestId") Long latestContestId, @RequestParam("studentId") Long studentId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Contest retrieved Successfully");
        response.setEntity(repository.contestsAfter(latestContestId, studentId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("search/invitees")
    public ResponseEntity<?> searchContestInvitees(@RequestParam("search") String search, @RequestParam() Integer count,
                                                   @RequestParam("classlevel") Long classlevel,
                                                   @RequestParam("studentId") Long studentId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Invitees retrieved Successfully");
        response.setEntity(studentAccountRepository.contestInvitees(search, count,  classlevel, studentId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
