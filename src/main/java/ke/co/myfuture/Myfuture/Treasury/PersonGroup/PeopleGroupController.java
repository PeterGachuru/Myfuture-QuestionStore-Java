package ke.co.myfuture.Myfuture.Treasury.PersonGroup;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Person.PersonRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("treasury/people-group")
public class PeopleGroupController {
    @Autowired
    PeopleGroupRepository repository;

    @Autowired
    PeopleGroupService accountService;

    @Autowired
    PersonRepository personRepository;

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
        Optional<PeopleGroup> peopleGroup = repository.findById(id);
        if (peopleGroup.isPresent()) {
            System.out.println("Is present ");
            peopleGroup.get().setMembers(personRepository.findPersonsByGroupId(id));
        }
        response.setEntity(peopleGroup.get());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("all")
    public ResponseEntity<?> fetchProductCategory(@RequestParam(required = false, name = "parentId") Long parentId) {
        System.out.println("all");
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("ProductCategory retrieved Successfully");
        List<PeopleGroup> accountList;
        if (parentId == null || parentId == 0)
            accountList = repository.findAllByAuditTrails_DeletedFlag(false, UserRequestContext.getCurrentUserName());
        else {
            accountList = repository.findAllByAuditTrails_DeletedFlag(false, parentId, UserRequestContext.getCurrentUserName());
        }
//        for (PeopleGroup account: accountList)
//            account.setAudits(repository.getAudits(account.getId()));
        response.setEntity(accountList);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
