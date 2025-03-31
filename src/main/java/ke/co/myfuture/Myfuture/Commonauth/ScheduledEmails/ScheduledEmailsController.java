package ke.co.myfuture.Myfuture.Commonauth.ScheduledLearnerEmails;


import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("scheduled-emails")
public class ScheduledEmailsController {

    @Autowired
    SchedulerService service;

    @Autowired
    ScheduledEmailsRepo scheduledEmailsRepo;
    @GetMapping
    public Page<ScheduledEmailsRepo.ScheduledEmailsProjection> getScheduledEmails(Pageable pageable) {
        return service.getPaginatedEmails(pageable);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchCurriTopic(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Retrieved Successfully");
        response.setEntity(scheduledEmailsRepo.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
