package ke.co.myfuture.Myfuture.UserManagement.Sender;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sender")
public class SenderController {
    @Autowired
    SenderRepository repository;

    @PostMapping("add")
    public ResponseEntity<?> newSender(@RequestBody Sender sender) {
        Sender savedSender = repository.save(sender);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedSender);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateSender(@RequestBody Sender sender) {
        Sender updatedSender = repository.save(sender);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedSender);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id/{senderId}")
    public ResponseEntity<?> fetchSender(@PathVariable("senderId") Long senderId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Sender retrieved Successfully");
        response.setEntity(repository.findById(senderId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getall/after/id/{senderId}")
    public ResponseEntity<?> fetchAllAfterSender(@PathVariable("senderId") Long senderId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Sender retrieved Successfully");
        response.setEntity(repository.sendersAfter(senderId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
