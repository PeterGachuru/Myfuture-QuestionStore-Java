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

    @DeleteMapping("delete")
    public ResponseEntity<?> deleteAccount(@RequestParam("accountId") Long id) {
        Optional<Account> account = repository.findById(id);
        if (account.isEmpty())
            return null;
        if (account.get().getBalance() != 0.0){
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Error");
            response.setMessage("Cannot delete");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            return  new ResponseEntity<>(response, HttpStatus.OK);
        }
        account.get().getAuditTrails().delete();
        repository.save(account.get());
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Deleted Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<?> fetchProductCategory(@RequestParam(required = false, name = "planId") Long planId,
                                                  @RequestParam(name = "groupId") Long groupId,
                                                  @RequestParam(name = "ownershipType") String ownershipType) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("ProductCategory retrieved Successfully");
        List<Account> accountList;
        if (ownershipType.equalsIgnoreCase("CASH") || planId == null || planId == 0) {
            System.out.println("get accounts by groupId: "+groupId+", ownershipType: "+ownershipType);
            accountList = repository.findAllByAuditTrails_DeletedFlag(false, groupId, ownershipType);
        } else {
            System.out.println();
            accountList = repository.findAllByAuditTrails_DeletedFlag(false, groupId, planId, ownershipType);
        }
//
        response.setEntity(accountList);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

