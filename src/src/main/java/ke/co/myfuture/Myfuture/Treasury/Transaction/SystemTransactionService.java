package ke.co.myfuture.Myfuture.Treasury.Transaction;

import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntry;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranType;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SystemTransactionService {
    @Autowired
    CommonTransactionService commonTransactionService;
    @Autowired
    TransactionPostingService transactionPostingService;

    Transaction createTransaction(TransactionBuilder transactionBuilder) {
        Transaction transaction = new Transaction();
        transaction.setCategory(transactionBuilder.getTransactionCategory());
        transaction.setAmountInvolved(transactionBuilder.getAmount());
        transaction.setTranDate(new Date());
        System.out.println("oneOfTheAccounts");
        transaction.setOneOfTheAccounts(transactionBuilder.getOneOfTheAccounts().getName());
        System.out.println(transaction.getOneOfTheAccounts());
        transaction.setContributionsPlan(transactionBuilder.getContributionsPlan());
        transaction.setHolderParticulars(transactionBuilder.getDebitParticulars());


        List<TranEntry> tranEntryList = new ArrayList<>();

        if (transactionBuilder.getCreditAccount() != null) {
            TranEntry creditTranEntry = new TranEntry();
            creditTranEntry.setTranType(TranType.CREDIT);
            creditTranEntry.setAmount(transactionBuilder.getAmount());
            creditTranEntry.setAccount(transactionBuilder.getCreditAccount());
            creditTranEntry.setParticulars(transactionBuilder.getCreditParticulars());
            tranEntryList.add(creditTranEntry);
        }

        if (transactionBuilder.getDebitAccount() != null){
            TranEntry debitTranEntry = new TranEntry();
            debitTranEntry.setTranType(TranType.DEBIT);
            debitTranEntry.setAmount(transactionBuilder.getAmount());
            debitTranEntry.setAccount(transactionBuilder.getDebitAccount());
            debitTranEntry.setParticulars(transactionBuilder.getDebitParticulars());
            tranEntryList.add(debitTranEntry);
        }

        transaction.setTranEntries(tranEntryList);

        return transaction;
    }



    public UniversalResponse saveTransaction(TransactionBuilder transactionBuilder) {
        if (transactionBuilder.getContributionsPlan() == null) {
            System.out.println("has no contributionPlan");
            return null;
        };
        Transaction transaction = createTransaction(transactionBuilder);
        commonTransactionService.attachOtherTranEntries(transaction);
        if (!transaction.balances()) {
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Error");
            response.setMessage("Transaction does not balance");
            response.setEntity(null);
            response.setStatusCode(HttpStatus.SC_NOT_ACCEPTABLE);
            return response;
        }
//        System.out.println(transaction);
        Transaction savedTransaction = transactionPostingService.saveNew(transaction);
        System.out.println(savedTransaction);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedTransaction);
        response.setStatusCode(201);
        return response;
    }
}
