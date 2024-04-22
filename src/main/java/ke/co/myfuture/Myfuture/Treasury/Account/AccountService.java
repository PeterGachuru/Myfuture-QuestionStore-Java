package ke.co.myfuture.Myfuture.Treasury.Account;

import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlanRepository;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.Person.PersonRepository;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroupRepository;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroupService;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    AccountRepository repository;

    @Autowired
    PeopleGroupRepository peopleGroupRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    ContributionsPlanRepository contributionsPlanRepository;
    public UniversalResponse saveAccount(Account account) {
        Optional<PeopleGroup> peopleGroup = peopleGroupRepository.findById(account.getGroupId());
        if (peopleGroup.isEmpty()) return null;
        account.setPeopleGroup(peopleGroup.get());
        if (account.getOwnershipType() == AccountOwnershipType.EXPENSE ||
                account.getOwnershipType() == AccountOwnershipType.INCOME) {
            if (account.getPlanId() == null)
                return null;
            Optional<ContributionsPlan> contributionsPlan = contributionsPlanRepository.findById(account.getPlanId());
            if (contributionsPlan.isEmpty()) return null;
            account.setContributionsPlan(contributionsPlan.get());
        }

        if (account.getOwnershipType() == AccountOwnershipType.EXPENSE ||
                account.getOwnershipType() == AccountOwnershipType.INCOME) {
            if (account.getPersonId() == null)
                return null;
            Optional<Person> person = personRepository.findById(account.getPersonId());
            if (person.isEmpty()) return null;
            account.setOwner(person.get());
        }


        Account savedAccount = repository.save(account);
        System.out.println(savedAccount);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedAccount);
        response.setStatusCode(201);
        return response;
    }

    public Account saveAutoCreatedAccount(Account account) {
        System.out.println("---saveAutoCreatedAccount--");
        account.setBalance(0.0);
        return repository.save(account);
    }
}
