package ke.co.myfuture.Myfuture.Treasury.Person;

import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.Person.PersonRepository;
import ke.co.myfuture.Myfuture.Treasury.Person.PersonService;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("treasury/person")
public class PersonController {
    @Autowired
    PersonRepository repository;

    @Autowired
    PersonService accountService;

    @PostMapping("add")
    public ResponseEntity<?> newPerson(@RequestBody Person account) {
        UniversalResponse response = accountService.savePerson(account);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updatePerson(@RequestBody Person account) {
        Person updatedPerson = repository.save(account);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedPerson);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchPerson(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Person retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("all")
    public ResponseEntity<?> fetchProductCategory(@RequestParam(required = false, name = "parentId") Long parentId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("ProductCategory retrieved Successfully");
        List<Person> accountList;
        if (parentId == 0)
            accountList = repository.findAllByAuditTrails_DeletedFlag(false);
        else {
            accountList = repository.findAllByAuditTrails_DeletedFlag(false, parentId);
        }
//        for (Person account: accountList)
//            account.setAudits(repository.getAudits(account.getId()));
        response.setEntity(accountList);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
