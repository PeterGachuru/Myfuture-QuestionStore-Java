package ke.co.myfuture.Myfuture.Treasury.Transaction;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Users.Users;
import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.utils.HttpInterceptor.UserDetailsRequestContext;
import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlanRepository;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntry;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranType;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository repository;

    @Autowired
    ContributionsPlanRepository contributionsPlanRepository;

    public UniversalResponse saveTransaction(Transaction transaction) {
        Optional<ContributionsPlan> contributionsPlan = contributionsPlanRepository.findById(transaction.getPlanId());
        if (contributionsPlan.isEmpty()) return null;
        transaction.setContributionsPlan(contributionsPlan.get());
        System.out.println(transaction);
        attachOtherTranEntries(transaction);
        Transaction savedTransaction = repository.save(transaction);
        System.out.println(savedTransaction);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedTransaction);
        response.setStatusCode(201);
        return response;
    }

    private void attachOtherTranEntries(Transaction transaction) {
        System.out.println("----attachOtherTranEntries----");
        if (transaction.getCategory() == TransactionCategory.EXPENSE) {
            Account expenseAccount = transaction.getContributionsPlan().getExpenseAccount();
            Double totalAmount = 0.0;
            for (TranEntry tranEntry: transaction.getTranEntries()) {
                if (tranEntry.getTranType() != TranType.CREDIT || tranEntry.getAmount() <=0 ) {
                    return;
                }
                totalAmount += tranEntry.getAmount();
                tranEntry.setAccount(expenseAccount);
            }
            addCashAccount(totalAmount, TranType.DEBIT, transaction, transaction.getContributionsPlan().getName()+" expenses ");
        }
    }

    private void addCashAccount(Double amount, TranType tranType, Transaction transaction, String particulars) {

        System.out.println("----addCashAccount----");
        Account cashAccount = getCashAccount(transaction.getContributionsPlan().getPeopleGroup());
        if (cashAccount == null)
            return;
        TranEntry tranEntry = new TranEntry();
        tranEntry.setAmount(amount);
        tranEntry.setTranType(tranType);
        tranEntry.setParticulars(particulars);
        tranEntry.setAccount(cashAccount);
        transaction.getTranEntries().add(tranEntry);
    }

    private Account getCashAccount(PeopleGroup peopleGroup) {
        System.out.println("----getCashAccount----");
        User user = UserRequestContext.getcurrentUser();

        System.out.println(user);
        return null;
    }
}
