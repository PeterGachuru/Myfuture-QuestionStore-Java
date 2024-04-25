package ke.co.myfuture.Myfuture.Treasury.Transaction;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountOwnershipType;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountRepository;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountService;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlanRepository;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.Person.PersonRepository;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntry;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntryRepository;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranType;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
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
        attachOtherTranEntries(transaction);
        if (!transaction.balances()) {
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Error");
            response.setMessage("Transaction does not balance");
            response.setEntity(null);
            response.setStatusCode(HttpStatus.SC_NOT_ACCEPTABLE);
            return response;
        }
        System.out.println(transaction);
        Transaction savedTransaction = save(transaction);
        System.out.println(savedTransaction);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedTransaction);
        response.setStatusCode(201);
        return response;
    }

    private Transaction save(Transaction transaction) {
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
        return repository.findById(savedTransaction.getId()).get();
    }
//    private Transaction save(Transaction transaction) {
//        List<TranEntry> tranEntryList = transaction.getTranEntries();
//        Transaction savedTransaction = repository.save(transaction);
//        savedTransaction.setTranEntries(new ArrayList<>());
//
//        for (TranEntry tranEntry: tranEntryList) {
//            tranEntry.setTransaction(savedTransaction);
//            savedTransaction.getTranEntries().add(tranEntryRepository.save(tranEntry));
//        }
//        return savedTransaction;
//    }

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

    private boolean attachOtherTranEntries(Transaction transaction) {
        System.out.println("----attachOtherTranEntries----");
        if (transaction.getCategory() == TransactionCategory.EXPENSE) {
            Account expenseAccount = transaction.getContributionsPlan().getExpenseAccount();
            Double totalAmount = 0.0;
            for (TranEntry tranEntry: transaction.getTranEntries()) {
                if (tranEntry.getTranType() != TranType.DEBIT || tranEntry.getAmount() <=0 ) {
                    return false;
                }
                totalAmount += tranEntry.getAmount();
                tranEntry.setAccount(expenseAccount);
            }
            addCashAccount(totalAmount, TranType.CREDIT, transaction, transaction.getContributionsPlan().getName()+" expenses ");
        } else if (transaction.getCategory() == TransactionCategory.INCOME) {
            Double totalAmount = 0.0;
            for (TranEntry tranEntry: transaction.getTranEntries()) {
                if (tranEntry.getTranType() != TranType.CREDIT || tranEntry.getAmount() <=0 ) {
                    return false;
                }
                totalAmount += tranEntry.getAmount();
                Optional<Account> account = accountRepository.findById(tranEntry.getAccountId());
                if (account.isEmpty())
                    return false;
                tranEntry.setAccount(account.get());
            }
            addCashAccount(totalAmount, TranType.DEBIT, transaction, transaction.getContributionsPlan().getName()+" expenses ");
        }
        return true;
    }

    private void addCashAccount(Double amount, TranType tranType, Transaction transaction, String particulars) {

        System.out.println("----addCashAccount----");
        Account cashAccount = getCashAccount(transaction.getContributionsPlan().getPeopleGroup());
        if (cashAccount == null) {
            System.out.println("Did not get cash account");
            return;
        }
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

        if (user == null) {
            System.out.println("Current user is null");
            return null;
        }

        Optional<Person> person = personRepository.findPersonByUserIdAndGroupId(user.getId(), peopleGroup.getId());

        if (person.isEmpty()) {
            System.out.println("User "+user.getId()+" not created in the group "+peopleGroup.getId());
            return null;
        }
        Optional<Account> account = accountRepository.findAccountForPersonByType(person.get().getId(), AccountOwnershipType.CASH);

        //create cash account for this user
        return account.orElseGet(() -> createCashAccount(person.get(), peopleGroup));
    }

    private Account createCashAccount(Person person, PeopleGroup peopleGroup) {
        Account account = new Account();
        account.setOwner(person);
        account.setPeopleGroup(peopleGroup);
        account.setName(person.getName()+" Cash");
        account.setPinPriority(1);
        account.setOwnershipType(AccountOwnershipType.CASH);

        return accountService.saveAutoCreatedAccount(account);
    }
}
