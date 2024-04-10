package ke.co.myfuture.Myfuture.Treasury.PersonGroup;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("treasury/people-group")
public class PeopleGroupController {
    @Autowired
    PeopleGroupRepository repository;

    @Autowired
    PeopleGroupService accountService;

    @PostMapping("add")
    public ResponseEntity<?> newPeopleGroup(@RequestBody PeopleGroup account) {
        UniversalResponse response = accountService.savePeopleGroup(account);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updatePeopleGroup(@RequestBody PeopleGroup account) {
        PeopleGroup updatedPeopleGroup = repository.save(account);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedPeopleGroup);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchPeopleGroup(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("PeopleGroup retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("all")
    public ResponseEntity<?> fetchProductCategory() {
        System.out.println("all");
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("ProductCategory retrieved Successfully");
        List<PeopleGroup> accountList = repository.findAllByAuditTrails_DeletedFlag(false);
//        for (PeopleGroup account: accountList)
//            account.setAudits(repository.getAudits(account.getId()));
        response.setEntity(accountList);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
