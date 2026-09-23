package ke.co.myfuture.Myfuture.Tuabudu.Song;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tuabudu/songs")
public class SongController {
    @Autowired
    SongRepository repository;
    @Autowired
    SongService songService;
    @PostMapping("add")
    public ResponseEntity<?> newSongAccount(@RequestBody Song song) {
        Song savedSong = repository.save(song);
        System.out.println(savedSong);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedSong);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateSong(@RequestBody Song user) {
        Song updatedSong = repository.save(user);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedSong);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchSong(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Song retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("get/all")
    public ResponseEntity<?> fetchAll() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Song retrieved Successfully");
        response.setEntity(repository.findAll());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
