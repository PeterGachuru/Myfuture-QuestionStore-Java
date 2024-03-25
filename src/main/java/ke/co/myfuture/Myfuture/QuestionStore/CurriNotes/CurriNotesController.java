package ke.co.myfuture.Myfuture.QuestionStore.CurriNotes;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("notes")
public class CurriNotesController {
    @Autowired
    CurriNotesRepository bookRepository;

    @PostMapping("add")
    public ResponseEntity<?> newCurriNotes(@RequestBody CurriNotes book) {
        if (book.id != null)
            return null;

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setStatusCode(201);
        CurriNotes savedCurriNotes = bookRepository.save(book);
        response.setEntity(savedCurriNotes);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateCurriNotes(@RequestBody CurriNotes notesFromUser) {
        if (notesFromUser.id == null)
            return null;
        Optional<CurriNotes> bookOptional = bookRepository.findById(notesFromUser.getId());

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setStatusCode(201);
        if (bookOptional.isPresent()) {
            CurriNotes bookDb = bookOptional.get();
            bookDb.update(notesFromUser);
            CurriNotes savedCurriNotes = bookRepository.save(bookDb);
            response.setEntity(savedCurriNotes);
        }else {
            response.setStatus("Error");
            response.setMessage("Could not save");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchCurriNotesById(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriNotes retrieved Successfully");
        Optional<CurriNotes> book = bookRepository.findById(id);
        response.setEntity(book.get());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<?> fetchCurriNotes() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriNotes retrieved Successfully");
        response.setEntity(bookRepository.findAll());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
