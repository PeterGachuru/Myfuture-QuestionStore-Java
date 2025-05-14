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
        System.out.println(account);
        if (account.getOwnershipType() == null) return null;
        Optional<PeopleGroup> peopleGroup = peopleGroupRepository.findById(account.getGroupId());
        if (peopleGroup.isEmpty()) return null;
        account.setPeopleGroup(peopleGroup.get());
        if (account.getOwnershipType() == AccountOwnershipType.EXPENSE ||
                account.getOwnershipType() == AccountOwnershipType.INCOME) {
            if (account.getPlanId() == null) {
                System.out.println("Plan Id is null");
                return null;
            }
            if (account.getOwnershipType() == AccountOwnershipType.INCOME) {
                if (account.getPersonId() == null) return null;
                Optional<Account> existingIncomeAccount = repository.findByPersonAndPlan(account.getPersonId(), account.getPlanId());
                if (existingIncomeAccount.isPresent()) {
                    UniversalResponse response = new UniversalResponse();
                    response.setStatus("Error");
                    response.setMessage("Person already has an income account with him");
                    response.setEntity(null);
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return response;
                }
            }
            Optional<ContributionsPlan> contributionsPlan = contributionsPlanRepository.findById(account.getPlanId());
            if (contributionsPlan.isEmpty()) {
                System.out.println("Did not find plan");
                return null;
            }
            account.setContributionsPlan(contributionsPlan.get());
        }

        if (account.getOwnershipType() == AccountOwnershipType.INCOME) {
            if (account.getPersonId() == null) {
                System.out.println("No person is attached");
                return null;
            }
            Optional<Person> person = personRepository.findById(account.getPersonId());
            if (person.isEmpty()) {
                System.out.println("No person found");
                return null;
            }
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
