package ke.co.myfuture.Myfuture.UserManagement.Contest;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInviteeRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestFriendship.ContestFriendshipRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import ke.co.myfuture.Myfuture.Commonauth.WebAdminAuth.StudentWebHandoffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    ContestFriendshipRepository contestFriendshipRepository;
    @Autowired
    ContestService contestService;
    @Autowired
    StudentWebHandoffService studentWebHandoffService;

    @PostMapping("add")
    public ResponseEntity<?> newContest(@RequestBody ContestService.CreateContest createContest) {
        Optional<Contest> savedContest = contestService.createContest(createContest);
        UniversalResponse response = new UniversalResponse();
        if (savedContest.isEmpty()) {
            response.setStatus("Failed");
            response.setMessage("Contest could not be created");
            response.setStatusCode(400);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedContest);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("savescores")
    public ResponseEntity<?> saveScores(@RequestBody ContestService.ScoresParentHolder scoresParentHolder) {
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
    public ResponseEntity<?> fetchAllAfterContest(@RequestParam("latestInviteId") Long latestInviteId,
                                                  @RequestParam("studentId") Long studentId) {
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
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Invitees retrieved Successfully");
        ibukaStudentAccountRepository.updateRecentActivity(studentId);
        List<Long> orderedInviteeIds =
                contestFriendshipRepository.contestInviteeIdsOrderedByRelationship(
                        search, 100, classlevel, studentId);

        // findAllById does not guarantee database ordering, so rebuild the
        // response in exactly the order returned by the relationship query.
        java.util.Map<Long, IbukaStudentAccount> studentsById =
                ibukaStudentAccountRepository.findAllById(orderedInviteeIds)
                        .stream()
                        .collect(java.util.stream.Collectors.toMap(
                                IbukaStudentAccount::getId,
                                student -> student
                        ));

        List<IbukaStudentAccount> orderedInvitees = orderedInviteeIds.stream()
                .map(studentsById::get)
                .filter(java.util.Objects::nonNull)
                .toList();

        response.setEntity(orderedInvitees);
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
        if (studentAccount.isEmpty()) {
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        }
        return new ResponseEntity<>(contestInviteeRepository.findByStudentaccount(studentAccount.get()), HttpStatus.OK);
    }

    /*
     * Android -> browser handoff links.
     *
     * The Android request must carry its normal Bearer JWT. The studentId
     * is checked against the authenticated parent before a short-lived
     * browser handoff token is issued.
     */
    @GetMapping("web-link/contests")
    public ResponseEntity<?> contestsWebLink(@RequestParam Long studentId) {
        return webLinkForStudent(studentId, StudentWebHandoffService.Action.CONTESTS, null);
    }

    @GetMapping("web-link/create")
    public ResponseEntity<?> createWebLink(@RequestParam Long studentId) {
        return webLinkForStudent(studentId, StudentWebHandoffService.Action.CREATE, null);
    }

    @GetMapping("web-link/attempt")
    public ResponseEntity<?> attemptWebLink(@RequestParam Long studentId,
                                            @RequestParam Long contestId) {
        if (!contestService.studentMayAccessContest(studentId, contestId)) {
            return new ResponseEntity<>("Student is not invited to this contest", HttpStatus.FORBIDDEN);
        }
        return webLinkForStudent(studentId, StudentWebHandoffService.Action.ATTEMPT, contestId);
    }

    @GetMapping("web-link/details")
    public ResponseEntity<?> detailsWebLink(@RequestParam Long studentId,
                                            @RequestParam Long contestId) {
        if (!contestService.studentMayAccessContest(studentId, contestId)) {
            return new ResponseEntity<>("Student is not invited to this contest", HttpStatus.FORBIDDEN);
        }
        return webLinkForStudent(studentId, StudentWebHandoffService.Action.DETAILS, contestId);
    }

    private ResponseEntity<?> webLinkForStudent(Long studentId,
                                                StudentWebHandoffService.Action action,
                                                Long resourceId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return new ResponseEntity<>("Android authentication is required", HttpStatus.UNAUTHORIZED);
        }

        if (!contestService.studentBelongsToAuthenticatedUser(studentId)) {
            return new ResponseEntity<>("Student does not belong to the authenticated account", HttpStatus.FORBIDDEN);
        }

        String email = UserRequestContext.getCurrentUserName();
        if (email == null || email.isBlank()) {
            return new ResponseEntity<>("Authenticated user could not be resolved", HttpStatus.UNAUTHORIZED);
        }

        String relativeUrl = studentWebHandoffService.createLink(email, studentId, action, resourceId);
        String url = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(relativeUrl)
                .build()
                .toUriString();

        return ResponseEntity.ok(new WebLinkResponse(url));
    }

    public record WebLinkResponse(String url) {}
}
