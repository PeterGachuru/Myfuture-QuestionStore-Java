package ke.co.myfuture.Myfuture.Treasury.GroupAccess;


import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("treasury/group-access")
public class GroupAccessController {
    @Autowired
    GroupAccessRepository repository;

    @Autowired
    GroupAccessService accountService;

    @PostMapping("add")
    public ResponseEntity<?> newGroupAccess(@RequestParam("email") String email,
                                       @RequestParam("groupId") Long groupId) {
        UniversalResponse response = accountService.saveGroupAccess(email, groupId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchGroupAccess(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("GroupAccess retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("all")
    public ResponseEntity<?> fetchProductCategory(@RequestParam("groupId") Long groupId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("ProductCategory retrieved Successfully");
        List<String> accountList = repository.findEmails(groupId);
        response.setEntity(accountList);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
