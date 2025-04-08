package ke.co.myfuture.Myfuture.Treasury.Person;

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
    PersonService personService;

    @PostMapping("add")
    public ResponseEntity<?> newPerson(@RequestBody Person account) {
        UniversalResponse response = personService.savePerson(account);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updatePerson(@RequestBody Person person) {
        Person personFromDb = repository.findById(person.getId()).get();
        personFromDb.update(person);
        Person updatedPerson = repository.save(personFromDb);

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
    @PutMapping("verifyPersonEmail")
    public ResponseEntity<?> verifyPersonEmail(@RequestParam("personId") Long personId, @RequestParam("groupId") Long groupId) {
        UniversalResponse response = personService.verifyPersonEmail(personId, groupId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("executeVerifyPersonEmail")
    public ResponseEntity<?> executeVerifyPersonEmail(@RequestParam("personId") Long personId, @RequestParam("groupId") Long groupId, @RequestParam("emailAddress") String emailAddress) {
        System.out.println("executeVerifyPersonEmail");

        UniversalResponse response = personService.executeVerifyPersonEmail(personId, groupId, emailAddress);
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
