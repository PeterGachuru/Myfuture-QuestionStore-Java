package ke.co.myfuture.Myfuture.Treasury.Transaction;


import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntryRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("treasury/transaction")
public class TransactionController {
    @Autowired
    TransactionRepository repository;

    @Autowired
    TransactionService transactionService;

    @Autowired
    TranEntryRepository tranEntryRepository;

    @PostMapping("add")
    public ResponseEntity<?> newTransaction(@RequestBody Transaction transaction) {
        UniversalResponse response = transactionService.saveTransaction(transaction);
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
        Optional<Transaction> transactionOptional = repository.findById(id);
        if (transactionOptional.isPresent()) {
            transactionOptional.get().setTranEntries(tranEntryRepository.findByTransactionId(transactionOptional.get().getId()));
        }
        response.setEntity(transactionOptional);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("reverse")
    public ResponseEntity<?> reverse(@RequestParam("id") Long id) {
        Optional<Transaction> transactionOptional = repository.findById(id);
        if (transactionOptional.isPresent()) {
            UniversalResponse universalResponse = transactionService.reverseTransaction(transactionOptional.get());
            return new ResponseEntity<>(universalResponse, HttpStatus.OK);
        }
        return null;
    }

    @GetMapping("all")
    public ResponseEntity<?> fetchProductCategory(@RequestParam("startDate") String startDate,
                                                  @RequestParam("endDate") String endDate,
                                                  @RequestParam("category") String category,
                                                  @RequestParam("planId") Long planId,
                                                  @RequestParam("groupId") Long groupId) {
        System.out.println("Fetching transactions");
        System.out.println("startDate: "+startDate);
        System.out.println("endDate: "+endDate);
        System.out.println("category: "+category);
        System.out.println("planId: "+planId);
        System.out.println("groupId: "+groupId);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Transactions retrieved Successfully");
        List<TransactionDTO> accountList = repository.findAllByAuditTrails_DeletedFlagOrderByAuditTrails_CreatedAtDesc(false, startDate, endDate, category, planId, groupId);
//        for (Transaction account: accountList)
//            account.setAudits(repository.getAudits(account.getId()));
        response.setEntity(accountList);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("allforgroup")
    public ResponseEntity<?> recentGroupTransactions(
                                                  @RequestParam("groupId") Long groupId) {
        System.out.println("Fetching transactions");
        System.out.println("groupId: "+groupId);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Transactions retrieved Successfully");
        List<TransactionDTO> accountList = repository.findAllRecentForGroupByAuditTrails_DeletedFlagOrderByAuditTrails_CreatedAtDesc(false, groupId, 40);
//        for (Transaction account: accountList)
//            account.setAudits(repository.getAudits(account.getId()));
        response.setEntity(accountList);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
