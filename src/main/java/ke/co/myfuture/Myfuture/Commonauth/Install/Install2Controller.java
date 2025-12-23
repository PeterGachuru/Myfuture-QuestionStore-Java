package ke.co.myfuture.Myfuture.Commonauth.Install;


import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

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
    public ResponseEntity<?> update(@RequestBody InstallUpdate installUpdate) {
        Optional<Install> install = repository.findById(installUpdate.getInstallId());
        if (install.isPresent()) {
            if (installUpdate.getFcmToken() != null) {
                install.get().setFcmToken(installUpdate.getFcmToken());
            }

            if (installUpdate.getAppVersion() != null && installUpdate.getAppVersion() > 1){
                install.get().setVersion(installUpdate.getAppVersion());
            }
            if (installUpdate.getAccountId() != null && installUpdate.getAccountId() > 1){
                install.get().setAccountId(installUpdate.getAccountId());
            }
            if (installUpdate.getAccountEmail() != null) {
                install.get().setAccountEmail(installUpdate.getAccountEmail());
                install.get().setAccountAddedAt(new Date());
            }
            install.get().setUpdatedAt(new Date());
            Install savedInstall = repository.save(install.get());
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Success");
            response.setMessage("Saved successfully");
            response.setEntity(savedInstall);
            response.setStatusCode(201);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return null;
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

    @GetMapping("all")
    public ResponseEntity<?> all() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Installs retrieved Successfully");
        response.setEntity(repository.findLatest300());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
