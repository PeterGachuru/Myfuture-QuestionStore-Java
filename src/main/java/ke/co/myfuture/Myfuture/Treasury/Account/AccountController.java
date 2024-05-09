package ke.co.myfuture.Myfuture.Treasury.Account;

import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("treasury/account")
public class AccountController {
    @Autowired
    AccountRepository repository;

    @Autowired
    AccountService accountService;

    @PostMapping("add")
    public ResponseEntity<?> newAccount(@RequestBody Account account) {
        UniversalResponse response = accountService.saveAccount(account);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateAccount(@RequestBody Account account) {

        Optional<Account> accountOptional = repository.findById(account.getId());
        if (accountOptional.isEmpty()){
            return null;
        }

        accountOptional.get().update(account);

        Account updatedAccount = repository.save(accountOptional.get());

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedAccount);
        response.setStatusCode(201);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchAccount(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Account retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("all")
    public ResponseEntity<?> fetchProductCategory(@RequestParam(required = false, name = "parentId") Long parentId,
                                                  @RequestParam(name = "ownershipType") String ownershipType) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("ProductCategory retrieved Successfully");
        List<Account> accountList;
        if (parentId == null || parentId == 0)
            accountList = repository.findAllByAuditTrails_DeletedFlag(false, ownershipType);
        else {
            accountList = repository.findAllByAuditTrails_DeletedFlag(false, parentId, ownershipType);
        }
//
        response.setEntity(accountList);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

