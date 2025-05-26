package ke.co.myfuture.Myfuture.Treasury.Transaction.TranValidators;

import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountRepository;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntry;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranType;
import ke.co.myfuture.Myfuture.Treasury.Transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class ExpenseValidator {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ValidatorCommons validatorCommons;

    public boolean attachOtherTranEntries(Transaction transaction) {
        Double totalAmount = 0.0;
        for (TranEntry tranEntry: transaction.getTranEntries()) {
            if (tranEntry.getTranType() != TranType.DEBIT || tranEntry.getAmount() <=0 ) {
                System.out.println("Is not debit");
                return false;
            }
            totalAmount += tranEntry.getAmount();
            Optional<Account> account = accountRepository.findById(tranEntry.getAccountId());
            if (account.isEmpty()) {
                System.out.println("Did not find account");
                return false;
            }
            tranEntry.setAccount(account.get());
        }
        return validatorCommons.addCashAccount(totalAmount, TranType.CREDIT, transaction, transaction.getContributionsPlan().getName()+" expenses ");
    }
}
