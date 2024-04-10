package ke.co.myfuture.Myfuture.Treasury.Transaction;


import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("treasury/transaction")
public class TransactionController {
    @Autowired
    TransactionRepository repository;

    @Autowired
    TransactionService accountService;

    @PostMapping("add")
    public ResponseEntity<?> newTransaction(@RequestBody Transaction account) {
        UniversalResponse response = accountService.saveTransaction(account);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateTransaction(@RequestBody Transaction account) {
        Transaction updatedTransaction = repository.save(account);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedTransaction);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchTransaction(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Transaction retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("all")
    public ResponseEntity<?> fetchProductCategory(@RequestParam("startDate") String startDate,
                                                  @RequestParam("endDate") String endDate) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("ProductCategory retrieved Successfully");
        List<Transaction> accountList = repository.findAllByAuditTrails_DeletedFlagOrderByAuditTrails_CreatedAtDesc(false, startDate, endDate);
//        for (Transaction account: accountList)
//            account.setAudits(repository.getAudits(account.getId()));
        response.setEntity(accountList);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
