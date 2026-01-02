package ke.co.myfuture.Myfuture.UserManagement.Cgroup;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cgroup")
public class CgroupController {
    @Autowired
    CgroupRepository repository;
    @PostMapping("add/")
    public ResponseEntity<?> newCgroup(@RequestBody Cgroup cgroup) {
        Cgroup savedCgroup = repository.save(cgroup);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedCgroup);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/")
    public ResponseEntity<?> updateCgroup(@RequestBody Cgroup cgroup) {
        Cgroup updatedCgroup = repository.save(cgroup);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedCgroup);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id/{cgroupId}")
    public ResponseEntity<?> fetchCgroup(@PathVariable("cgroupId") Long cgroupId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Cgroup retrieved Successfully");
        response.setEntity(repository.findById(cgroupId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
