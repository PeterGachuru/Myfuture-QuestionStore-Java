package ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion;

import ke.co.myfuture.Myfuture.ImageStore.FileManagement.ImageFile;
import ke.co.myfuture.Myfuture.ImageStore.FileManagement.ImageFileService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestion;
import ke.co.myfuture.Myfuture.QuestionStore.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@CrossOrigin
@RequestMapping("questions")

public class CurriQuestionController {
    @Autowired
    CurriQuestionRepository repository;

    @Autowired
    ImageFileService imageFileService;

    @PostMapping("add/")
    public ResponseEntity<?> newCurriQuestion(@RequestBody CurriQuestion question) {
        CurriQuestion savedCurriQuestion = repository.save(question);
        System.out.println(savedCurriQuestion);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedCurriQuestion);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateCurriQuestion(@RequestBody CurriQuestion question) {
        Optional<CurriQuestion> dbCurriQuestion = repository.findById(question.id);
        if (dbCurriQuestion.isPresent()) {
            CurriQuestion curriQuestion = dbCurriQuestion.get();
            curriQuestion.setString(question.getString());
            curriQuestion.setHasImage(question.getHasImage());
            curriQuestion.setImageCode(question.getImageCode());
            curriQuestion.setImageLevel(question.getImageLevel());
            curriQuestion.setChoices(question.getChoices());
            CurriQuestion savedSubquestion = repository.save(curriQuestion);
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Success");
            response.setMessage("Updated Successfully");
            response.setEntity(savedSubquestion);
            response.setStatusCode(201);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Could not update");
        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchCurriQuestion(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriQuestion retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/subtopic")
    public ResponseEntity<?> fetchCurriQuestionByParent(@RequestParam("subtopicId") Long subtopicId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriQuestion retrieved Successfully");
        response.setEntity(repository.findBySubtopicId(subtopicId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(path = "attach-image/{id}", method = POST,  consumes = { MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> newFile(@RequestParam("image") MultipartFile fileUploaded, @PathVariable Long id) {
        ImageFile imageFile = imageFileService.save(fileUploaded);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        Optional<CurriQuestion> curriNormalChoice = repository.findById(id);
        if (imageFile != null && curriNormalChoice.isPresent()) {
            curriNormalChoice.get().setHasImage(true);
            curriNormalChoice.get().setImageCode(imageFile.getCode());
            repository.save(curriNormalChoice.get());
            response.setStatus("Success");
            response.setMessage("Saved successfully");
            response.setEntity(curriNormalChoice.get());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
