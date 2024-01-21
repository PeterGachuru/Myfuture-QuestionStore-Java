package ke.co.myfuture.Myfuture.QuestionStore.CurriNormalChoice;


import ke.co.myfuture.Myfuture.ImageStore.FileManagement.ImageFile;
import ke.co.myfuture.Myfuture.ImageStore.FileManagement.ImageFileService;
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
@RequestMapping("normal-choice")

public class CurriNormalChoiceController {
    @Autowired
    CurriNormalChoiceRepository repository;

    @Autowired
    ImageFileService imageFileService;

    @RequestMapping(path = "attach-image/{id}", method = POST,  consumes = { MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> newFile( @RequestParam("uploadfile") MultipartFile fileUploaded, @PathVariable Long id) {
        ImageFile imageFile = imageFileService.save(fileUploaded);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        Optional<CurriNormalChoice> curriNormalChoice = repository.findById(id);
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
