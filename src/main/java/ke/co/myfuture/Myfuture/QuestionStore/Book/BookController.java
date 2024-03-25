package ke.co.myfuture.Myfuture.QuestionStore.Book;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("book")
public class BookController {
    @Autowired
    BookRepository bookRepository;

    @PostMapping("add")
    public ResponseEntity<?> newBook(@RequestBody Book book) {
        if (book.id != null)
            return null;

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setStatusCode(201);
            Book savedBook = bookRepository.save(book);
            response.setEntity(savedBook);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateBook(@RequestBody Book bookFromUser) {
        if (bookFromUser.id == null)
            return null;
        Optional<Book> bookOptional = bookRepository.findById(bookFromUser.getId());

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setStatusCode(201);
        if (bookOptional.isPresent()) {
            Book bookDb = bookOptional.get();
            bookDb.update(bookFromUser);
            Book savedBook = bookRepository.save(bookDb);
            response.setEntity(savedBook);
        }else {
            response.setStatus("Error");
            response.setMessage("Could not save");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchBookById(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Book retrieved Successfully");
        Optional<Book> book = bookRepository.findById(id);
        response.setEntity(book.get());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<?> fetchBook() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Book retrieved Successfully");
        response.setEntity(bookRepository.findAll());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
