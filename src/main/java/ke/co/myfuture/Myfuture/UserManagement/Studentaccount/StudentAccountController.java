package ke.co.myfuture.Myfuture.UserManagement.Studentaccount;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("student")
public class StudentAccountController {
    @Autowired
    StudentAccountRepository repository;

    @PostMapping("add")
    public ResponseEntity<?> newStudentAccount(@RequestBody IbukaStudentAccount student) {
        if (student.id != null) return null;

        IbukaStudentAccount savedStudentAccount = repository.save(student);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedStudentAccount);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/")
    public ResponseEntity<?> updateStudentAccount(@RequestBody IbukaStudentAccount student) {
        if (student.id == null) return null;
        IbukaStudentAccount updatedStudentAccount = repository.save(student);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedStudentAccount);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id/{studentId}")
    public ResponseEntity<?> fetchStudentAccount(@PathVariable("studentId") Long studentId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("StudentAccount retrieved Successfully");
        response.setEntity(repository.findById(studentId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/parent")
    public ResponseEntity<?> fetchAll(@RequestParam("parentId") Long parentId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("StudentAccount retrieved Successfully");
        response.setEntity(repository.findByParent(parentId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
