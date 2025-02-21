package ke.co.myfuture.Myfuture.Treasury.Transaction;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountOwnershipType;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountRepository;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountService;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlanRepository;
import ke.co.myfuture.Myfuture.Treasury.GroupAccess.GroupAccess;
import ke.co.myfuture.Myfuture.Treasury.GroupAccess.GroupAccessService;
import ke.co.myfuture.Myfuture.Treasury.Person.PersonRepository;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntry;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntryRepository;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranType;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Autowired
    GroupAccessService groupAccessService;

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
        Transaction savedTransaction = saveNew(transaction);
        System.out.println(savedTransaction);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedTransaction);
        response.setStatusCode(201);
        return response;
    }


    private Transaction saveNew(Transaction transaction) {
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

    private Transaction saveReversal(Transaction transaction) {
        List<TranEntry> tranEntryList = transaction.getTranEntries();
        transaction.setTranEntries(new ArrayList<>());
        Transaction savedTransaction = repository.save(transaction);
        for (TranEntry tranEntry: tranEntryList) {
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

        Transaction transaction1 = saveReversal(reversalTransaction);
        repository.save(transaction);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Reversed successfully");
        response.setEntity(transaction1);
        response.setStatusCode(HttpStatus.SC_OK);
        return response;
    }

    private boolean attachOtherTranEntries(Transaction transaction) {
        System.out.println("----attachOtherTranEntries----");
        if (transaction.getCategory() == TransactionCategory.EXPENSE) {
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
            addCashAccount(totalAmount, TranType.DEBIT, transaction, transaction.getContributionsPlan().getName()+" income ");
        } else  if (transaction.getCategory() == TransactionCategory.LEND) {
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
            addCashAccount(totalAmount, TranType.CREDIT, transaction, transaction.getContributionsPlan().getName()+" loan out ");
        } else  if (transaction.getCategory() == TransactionCategory.REPAY_LEND) {
            Double totalAmount = 0.0;
            for (TranEntry tranEntry: transaction.getTranEntries()) {
                if (tranEntry.getTranType() != TranType.CREDIT || tranEntry.getAmount() <=0 ) {
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
            addCashAccount(totalAmount, TranType.DEBIT, transaction, transaction.getContributionsPlan().getName()+" loan out ");
        } else if (transaction.getCategory() == TransactionCategory.BORROW) {
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
            addCashAccount(totalAmount, TranType.DEBIT, transaction, transaction.getContributionsPlan().getName()+" loan in ");
        } else if (transaction.getCategory() == TransactionCategory.REPAY_BORROW) {
            Double totalAmount = 0.0;
            for (TranEntry tranEntry: transaction.getTranEntries()) {
                if (tranEntry.getTranType() != TranType.DEBIT || tranEntry.getAmount() <=0 ) {
                    return false;
                }
                totalAmount += tranEntry.getAmount();
                Optional<Account> account = accountRepository.findById(tranEntry.getAccountId());
                if (account.isEmpty())
                    return false;
                tranEntry.setAccount(account.get());
            }
            addCashAccount(totalAmount, TranType.CREDIT, transaction, transaction.getContributionsPlan().getName()+" loan in ");
        }
        return true;
    }

    private void addCashAccount(Double amount, TranType tranType, Transaction transaction, String particulars) {
        System.out.println("----addCashAccount----");
        Account cashAccount = getCashAccount(transaction.getContributionsPlan().getId(), transaction.getContributionsPlan().getPeopleGroup());
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

    private Account getCashAccount(Long planId, PeopleGroup peopleGroup) {
        System.out.println("----getCashAccount----");
        User user = UserRequestContext.getcurrentUser();

        if (user == null) {
            System.out.println("Current user is null");
            return null;
        }

//        Optional<Person> person = personRepository.findPersonByUserIdAndGroupId(user.getId(), peopleGroup.getId());
        Optional<GroupAccess> groupAccess = groupAccessService.findGroupAccess(user.getEmail(), peopleGroup.getId());

        if (groupAccess.isEmpty()) {
            System.out.println("User "+user.getId()+" does not have access to the group "+peopleGroup.getId());
            return null;
        }

        Optional<Account> account = accountRepository.findAccountForPersonByTypeAndPlanId(groupAccess.get().getPerson().getId(), planId,  peopleGroup.getId(),AccountOwnershipType.CASH.name());

        if (account.isEmpty()) {
            System.out.println("cash account is empty, will need to create");
        }

        //create cash account for this user
        return account.orElseGet(() -> createCashAccount(groupAccess.get(), peopleGroup, planId));
    }

    private Account createCashAccount(GroupAccess groupAccess, PeopleGroup peopleGroup, Long planId) {
        Account account = new Account();
        account.setOwner(groupAccess.getPerson());
        account.setPeopleGroup(peopleGroup);
        account.setName(groupAccess.getPerson().getName()+" Cash");
        account.setPinPriority(1);
        account.setPlanId(planId);
        account.setContributionsPlan(contributionsPlanRepository.findById(planId).get());
        account.setOwnershipType(AccountOwnershipType.CASH);

        Account account1 = accountService.saveAutoCreatedAccount(account);

        return account1;
    }
}
