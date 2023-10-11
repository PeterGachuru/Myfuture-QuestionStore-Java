package ke.co.myfuture.Myfuture.ImageStore.FileManagement;

import ke.co.myfuture.Myfuture.QuestionStore.Response.UniversalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@CrossOrigin(origins = "*", maxAge = 3600)
//@RestController
@Slf4j
@RequestMapping("/api/v1/images/")
public class ImageFileController {
    @Autowired
    ImageFileService service;

//    @Autowired
//    private PdfService pdfService;
    @Autowired
    ImageFileRepository repository;

    @RequestMapping(path = "upload", method = POST,  consumes = { MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> newFile( @RequestParam("uploadfile") MultipartFile fileUploaded) {
        ImageFile imageFile = new ImageFile();
        try {
            imageFile.imageContent = fileUploaded.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ImageFile savedImageFile = repository.save(imageFile);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedImageFile);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateFile(@PathVariable("id") long id, @RequestBody ImageFile imageFile){
        if (id != imageFile.id)
            throw new RuntimeException("Error: File ID is not matching");
        ImageFile updatedImageFile = repository.findById(id).map(imageFileInDB -> repository.save(imageFile))
                .orElseThrow(() -> new RuntimeException("Error: File with id " + id+" not found"));

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("File updated Successfully");
        response.setEntity(updatedImageFile);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/{id}")
    public ResponseEntity<?> fetchFile(@PathVariable long id) {
        ImageFile imageFile = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: File with id " + id+" not found"));

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("File retrieved Successfully");
        response.setEntity(imageFile);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getimage/{id}")
    public ResponseEntity<byte[]> fetchFileDirect(@PathVariable long id) {
        ImageFile imageFile = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: File with id " + id+" not found"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(imageFile.contentType));
        headers.setContentDispositionFormData("inline", imageFile.fileName);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(imageFile.imageContent,
                headers, HttpStatus.OK);

        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll(){
        try {
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Success");
            response.setMessage("Document libraries retrieved Successfully");
            response.setEntity(repository.findAll());
            response.setStatusCode(200);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Error {} " + e);
            return null;
        }
    }
}