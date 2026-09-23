package ke.co.myfuture.Myfuture.Treasury.Transaction.TranValidators;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountOwnershipType;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountRepository;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountService;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlanRepository;
import ke.co.myfuture.Myfuture.Treasury.GroupAccess.GroupAccess;
import ke.co.myfuture.Myfuture.Treasury.GroupAccess.GroupAccessService;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntry;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranType;
import ke.co.myfuture.Myfuture.Treasury.Transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ValidatorCommons {
    @Autowired
    AccountService accountService;
    @Autowired
    GroupAccessService groupAccessService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ContributionsPlanRepository contributionsPlanRepository;

    public boolean addCashAccount(Double amount, TranType tranType, Transaction transaction, String particulars) {
        System.out.println("----addCashAccount----");
        Account cashAccount = getCashAccount(transaction.getContributionsPlan().getId(), transaction.getContributionsPlan().getPeopleGroup());
        if (cashAccount == null) {
            System.out.println("Did not get cash account");
            return false;
        }
        TranEntry tranEntry = new TranEntry();
        tranEntry.setAmount(amount);
        tranEntry.setTranType(tranType);
        tranEntry.setParticulars(particulars);
        tranEntry.setAccount(cashAccount);
        transaction.getTranEntries().add(tranEntry);
        return true;
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

        Optional<Account> account = accountRepository.findAccountForPersonByTypeAndPlanId(groupAccess.get().getPerson().getId(), planId,  peopleGroup.getId(), AccountOwnershipType.CASH.name());

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