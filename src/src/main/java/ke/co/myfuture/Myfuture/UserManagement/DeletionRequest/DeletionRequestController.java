package ke.co.myfuture.Myfuture.UserManagement.DeletionRequest;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("delete-request")
public class DeletionRequestController {
    @Autowired
    DeletionRequestRepo repository;


    @PostMapping("add/")
    public ResponseEntity<?> newDeletionRequestAccount(@RequestBody DeletionRequest user) {
        DeletionRequest savedDeletionRequest = repository.save(user);
        System.out.println(savedDeletionRequest);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedDeletionRequest);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/")
    public ResponseEntity<?> updateDeletionRequest(@RequestBody DeletionRequest user) {
        DeletionRequest updatedDeletionRequest = repository.save(user);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedDeletionRequest);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchDeletionRequest(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("DeletionRequest retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
