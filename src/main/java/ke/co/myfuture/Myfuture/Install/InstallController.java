package ke.co.myfuture.Myfuture.Install;


import ke.co.myfuture.Myfuture.Response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("install")
public class InstallController {
    @Autowired
    InstallRepository repository;

    @PostMapping("add/")
    public ResponseEntity<?> newInstall(@RequestBody Install contest) {
        Install savedInstall = repository.save(contest);
        ApiResponse response = new ApiResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedInstall);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/")
    public ResponseEntity<?> updateInstall(@RequestBody Install contest) {
        Install updatedInstall = repository.save(contest);

        ApiResponse response = new ApiResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedInstall);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id/{contestId}")
    public ResponseEntity<?> fetchInstall(@PathVariable("contestId") Long contestId) {
        ApiResponse response = new ApiResponse();
        response.setStatus("Success");
        response.setMessage("Install retrieved Successfully");
        response.setEntity(repository.findById(contestId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
