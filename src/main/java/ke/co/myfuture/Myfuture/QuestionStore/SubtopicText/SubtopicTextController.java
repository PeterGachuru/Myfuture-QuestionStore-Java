package ke.co.myfuture.Myfuture.QuestionStore.SubtopicText;

import ke.co.myfuture.Myfuture.QuestionStore.Response.UniversalResponse;
import ke.co.myfuture.Myfuture.QuestionStore.SubtopicText.SubtopicText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("subtopictext")
public class SubtopicTextController {
    @Autowired
    SubtopicTextRepository repository;
    @PostMapping("add/")
    public ResponseEntity<?> newSubtopicTextAccount(@RequestBody SubtopicText user) {
        SubtopicText savedSubtopicText = repository.save(user);
        System.out.println(savedSubtopicText);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedSubtopicText);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/")
    public ResponseEntity<?> updateSubtopicText(@RequestBody SubtopicText user) {
        SubtopicText updatedSubtopicText = repository.save(user);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedSubtopicText);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchSubtopicText(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("SubtopicText retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
