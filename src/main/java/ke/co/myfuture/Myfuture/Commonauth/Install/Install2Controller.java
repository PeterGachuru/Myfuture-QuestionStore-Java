package ke.co.myfuture.Myfuture.Commonauth.Install;


import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("interaction/install")
public class Install2Controller {
    @Autowired
    Install2Repository repository;

    @PostMapping("add")
    public ResponseEntity<?> newInstall(@RequestBody Install install) {
        System.out.println("-------new install------");
        System.out.println(install);
        Install savedInstall = repository.save(install);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedInstall);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateInstall(@RequestBody Install install) {
        Install updatedInstall = repository.save(install);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedInstall);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id/{installId}")
    public ResponseEntity<?> fetchInstall(@PathVariable("installId") Long installId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Install retrieved Successfully");
        response.setEntity(repository.findById(installId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
