package ke.co.myfuture.Myfuture.Tuabudu.Singlist;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tuabudu/singlist")
public class SinglistController {
    @Autowired
    SinglistRepository repository;
    @Autowired
    SinglistService singlistService;

    @PostMapping("add")
    public ResponseEntity<?> newPlaylistAccount(@RequestBody Singlist user) {
        Singlist savedSinglist = repository.save(user);
        System.out.println(savedSinglist);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedSinglist);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updatePlaylist(@RequestBody Singlist user) {
        Singlist updatedSinglist = repository.save(user);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedSinglist);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchPlaylist(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Playlist retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("get/all")
    public ResponseEntity<?> fetchAll() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Playlist retrieved Successfully");
        response.setEntity(repository.findAll());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
