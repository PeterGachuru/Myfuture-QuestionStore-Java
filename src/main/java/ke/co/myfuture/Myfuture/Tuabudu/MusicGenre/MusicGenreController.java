package ke.co.myfuture.Myfuture.Tuabudu.MusicGenre;

import ke.co.myfuture.Myfuture.Tuabudu.Language.Language;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public class MusicGenreController {

    @Autowired
    MusicGenreRepository repository;
    @Autowired
    MusicGenreService musicGenreService;

    @PostMapping("add")
    public ResponseEntity<?> newSingerAccount(@RequestBody MusicGenre user) {
        MusicGenre savedSinger = repository.save(user);
        System.out.println(savedSinger);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedSinger);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateSinger(@RequestBody MusicGenre user) {
        MusicGenre updatedSinger = repository.save(user);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedSinger);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchSinger(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Singer retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("get/all")
    public ResponseEntity<?> fetchAll() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Singer retrieved Successfully");
        response.setEntity(repository.findAll());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
