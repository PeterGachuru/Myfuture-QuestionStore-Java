package ke.co.myfuture.Myfuture.Useraccount;

import ke.co.myfuture.Myfuture.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserAccountController {
    @Autowired
    UserAccountRepository repository;

    @PostMapping("add/")
    public ResponseEntity<?> newUserAccount(@RequestBody UserAccount user) {
        UserAccount savedUserAccount = repository.save(user);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedUserAccount);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/")
    public ResponseEntity<?> updateUserAccount(@RequestBody UserAccount user) {
        UserAccount updatedUserAccount = repository.save(user);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedUserAccount);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id/{userId}")
    public ResponseEntity<?> fetchUserAccount(@PathVariable("userId") Long userId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("UserAccount retrieved Successfully");
        response.setEntity(repository.findById(userId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
