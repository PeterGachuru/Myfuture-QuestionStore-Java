package ke.co.myfuture.Myfuture.UserManagement.Broadcast;

import ke.co.myfuture.Myfuture.Commonauth.Utils.CustomMailSender;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("learning/broadcast")
public class BroadcastController {
    @Autowired
    BroadcastRepository repository;
    @Autowired
    BroadcastService broadcastService;

    @Autowired
    CustomMailSender customMailSender;

    @PostMapping("add")
    public ResponseEntity<?> newWritersbroadcastAccount(@RequestBody Broadcast broadcast) {
        Broadcast savedBroadcast = repository.save(broadcast);
        System.out.println(savedBroadcast);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedBroadcast);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("test")
    public ResponseEntity<?> testWritersbroadcast(@RequestBody Broadcast broadcast) {
        System.out.println(broadcast);

        customMailSender.sendEmail(broadcast.subject,
                broadcast.getHtml(),
                new String[]{broadcast.getTestEmail()}, new String[]{}, new String[]{}, "Ibuka Technologies");
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Sent successfully");
        response.setEntity(broadcast);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateWritersbroadcast(@RequestBody Broadcast broadcast) {
        Optional<Broadcast> broadcastFromDB = repository.findById(broadcast.getId());
        if (broadcastFromDB.isEmpty()) return null;
        broadcastFromDB.get().update(broadcast);
        Broadcast updatedBroadcast = repository.save(broadcastFromDB.get());

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedBroadcast);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("send")
    public ResponseEntity<?> updateWritersbroadcast(@RequestParam("id") Long id) {
        Broadcast updatedBroadcast = repository.findById(id).get();
        if (updatedBroadcast.getSentBy() != null)
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        Boolean result = broadcastService.broadCastToStudents(updatedBroadcast);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedBroadcast);
        response.setStatusCode(200);
        if (!result)
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("write")
    public ResponseEntity<?> writeEmail(@RequestParam String email,
                                        @RequestParam String subject,
                                        @RequestParam String message) {
        broadcastService.sendEmail(email, subject, message);
        return new ResponseEntity<>("Sent email", HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchWritersbroadcast(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Writersbroadcast retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<?> fetchWritersbroadcasts() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Writersbroadcast retrieved Successfully");
        response.setEntity(repository.findAll());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
