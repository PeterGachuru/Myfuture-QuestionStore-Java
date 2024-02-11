package ke.co.myfuture.Myfuture.QuestionStore.Writersbroadcast;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("writersbroadcast")
public class WritersbroadcastController {
    @Autowired
    WritersbroadcastRepository repository;
    @Autowired
    WritersbroadcastService writersbroadcastService;

    @PostMapping("add/")
    public ResponseEntity<?> newWritersbroadcastAccount(@RequestBody Writersbroadcast user) {
        Writersbroadcast savedWritersbroadcast = repository.save(user);
        System.out.println(savedWritersbroadcast);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedWritersbroadcast);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/")
    public ResponseEntity<?> updateWritersbroadcast(@RequestBody Writersbroadcast user) {
        Writersbroadcast updatedWritersbroadcast = repository.save(user);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedWritersbroadcast);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("write/")
    public ResponseEntity<?> writeEmail(@RequestParam String email, @RequestParam String subject, @RequestParam String message) {
        writersbroadcastService.sendEmail(email, subject, message);
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
}
