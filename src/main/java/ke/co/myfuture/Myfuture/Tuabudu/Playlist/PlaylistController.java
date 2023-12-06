package ke.co.myfuture.Myfuture.Tuabudu.Playlist;

import ke.co.myfuture.Myfuture.QuestionStore.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("playlist")

public class PlaylistController {
    @Autowired
    PlaylistRepository repository;
    @Autowired
    PlaylistService playlistService;

    @PostMapping("add/")
    public ResponseEntity<?> newPlaylistAccount(@RequestBody Playlist user) {
        Playlist savedPlaylist = repository.save(user);
        System.out.println(savedPlaylist);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedPlaylist);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/")
    public ResponseEntity<?> updatePlaylist(@RequestBody Playlist user) {
        Playlist updatedPlaylist = repository.save(user);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedPlaylist);
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
