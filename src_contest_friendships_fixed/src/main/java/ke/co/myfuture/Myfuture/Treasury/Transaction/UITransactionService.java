package ke.co.myfuture.Myfuture.Treasury.Transaction;

import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountRepository;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountService;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlanRepository;
import ke.co.myfuture.Myfuture.Treasury.GroupAccess.GroupAccessService;
import ke.co.myfuture.Myfuture.Treasury.Person.PersonRepository;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntry;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntryRepository;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranType;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranValidators.*;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UITransactionService {
    @Autowired
    TransactionRepository repository;

    @Autowired
    ContributionsPlanRepository contributionsPlanRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    TranEntryRepository tranEntryRepository;

    @Autowired
    GroupAccessService groupAccessService;

    @Autowired
    TransactionPostingService transactionPostingService;
    @Autowired
    CommonTransactionService commonTransactionService;

    public UniversalResponse error(String message) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Error");
        response.setMessage("Transaction does not balance");
        response.setEntity(null);
        response.setStatusCode(HttpStatus.SC_NOT_ACCEPTABLE);
        return response;
    }

    public UniversalResponse saveTransaction(Transaction transaction) {
        Optional<ContributionsPlan> contributionsPlan = contributionsPlanRepository.findById(transaction.getPlanId());
        if (contributionsPlan.isEmpty()) return null;
        transaction.setContributionsPlan(contributionsPlan.get());
        if(!attachAccounts(transaction)) {
            return error("Account not found");
        }
        commonTransactionService.attachOtherTranEntries(transaction);
        if (!transaction.balances()) {
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Error");
            response.setMessage("Transaction does not balance");
            response.setEntity(null);
            response.setStatusCode(HttpStatus.SC_NOT_ACCEPTABLE);
            return response;
        }
        System.out.println(transaction);
        Transaction savedTransaction = transactionPostingService.saveNew(transaction);
        System.out.println(savedTransaction);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedTransaction);
        response.setStatusCode(201);
        return response;
    }

    private boolean attachAccounts(Transaction transaction) {
        System.out.println("---attachAccounts--");
        for (TranEntry tranEntry: transaction.getTranEntries()) {
            Optional<Account> account = accountRepository.findById(tranEntry.getAccountId());
            if (account.isEmpty())
                return false;
            System.out.println("Found account");
            System.out.println(account.get());
            tranEntry.setAccount(account.get());
        }
        System.out.println("---end attachAccounts--");

        return true;
    }

    public UniversalResponse reverseTransaction(Transaction transaction) {
        if (transaction.reversal != null && transaction.reversal) {
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Error");
            response.setMessage("Already reversed");
            response.setEntity(null);
            response.setStatusCode(HttpStatus.SC_NOT_ACCEPTABLE);
            return response;
        }
        Transaction reversalTransaction = new Transaction(transaction);
        reversalTransaction.setCategory(TransactionCategory.REVERSAL);
        reversalTransaction.reversalFor = transaction.getId();
        reversalTransaction.setReversal(true);
        reversalTransaction.setTranDate(new Date());
        transaction.setReversal(true);

        reversalTransaction.setTranEntries(new ArrayList<>());

        TranEntry newEntry;
        for (TranEntry tranEntry: transaction.getTranEntries()){
            newEntry = new TranEntry();
            newEntry.setAmount(tranEntry.getAmount());
            newEntry.setAccount(tranEntry.getAccount());
            newEntry.setAccountId(tranEntry.getAccountId());
            newEntry.setAccountName(tranEntry.getAccountName());
            newEntry.setTranType(tranEntry.getTranType() == TranType.CREDIT? TranType.DEBIT: TranType.CREDIT);
            newEntry.setParticulars("Reversed: "+transaction.getId());

            reversalTransaction.getTranEntries().add(newEntry);
        }

        if (!reversalTransaction.balances()) {
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Error");
            response.setMessage("Transaction does not balance");
            response.setEntity(null);
            response.setStatusCode(HttpStatus.SC_NOT_ACCEPTABLE);
            return response;
        }

        Transaction transaction1 = transactionPostingService.saveReversal(reversalTransaction);
        repository.save(transaction);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Reversed successfully");
        response.setEntity(transaction1);
        response.setStatusCode(HttpStatus.SC_OK);
        return response;
    }
}
