package ke.co.myfuture.Myfuture.Treasury.Transaction;

import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountRepository;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntry;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntryRepository;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionPostingService {
    @Autowired
    TranEntryRepository tranEntryRepository;
    @Autowired
    TransactionRepository repository;

    @Autowired
    AccountRepository accountRepository;
    public Transaction saveNew(Transaction transaction) {
        List<TranEntry> tranEntryList = transaction.getTranEntries();
        transaction.setTranEntries(new ArrayList<>());
        Transaction savedTransaction = repository.save(transaction);
        for (TranEntry tranEntry: tranEntryList) {
            Account account = tranEntry.getAccount();
            tranEntry.setAccountId(account.getId());
            tranEntry.setAccountName(account.getName());
            tranEntry.setTransaction(savedTransaction);
            tranEntryRepository.save(tranEntry);
        }
        return post(savedTransaction.getId());
    }

    @Transactional
    private Transaction post(Long id) {
        Optional<Transaction> transaction = repository.findById(id);
        if (transaction.isEmpty()) return null;
        List<TranEntry> tranEntryList = transaction.get().getTranEntries();
        for (TranEntry tranEntry: tranEntryList) {
            Optional<Account> account = accountRepository.findById(tranEntry.getAccountId());
            if (account.isEmpty()) return null;
            Double currentBalance = account.get().getBalance();
            Double newBalance = currentBalance + (tranEntry.getTranType() == TranType.DEBIT? -1*tranEntry.getAmount(): tranEntry.getAmount());
            account.get().setBalance(newBalance);
            accountRepository.save(account.get());
        }
        return transaction.get();
    }

    public Transaction saveReversal(Transaction transaction) {
        List<TranEntry> tranEntryList = transaction.getTranEntries();
        transaction.setTranEntries(new ArrayList<>());
        Transaction savedTransaction = repository.save(transaction);
        for (TranEntry tranEntry: tranEntryList) {
            tranEntry.setTransaction(savedTransaction);
            tranEntryRepository.save(tranEntry);
        }
        return post(savedTransaction.getId());
    }
}
