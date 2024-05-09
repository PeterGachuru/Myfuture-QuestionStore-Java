package ke.co.myfuture.Myfuture.Treasury.ContributionsPlan;

import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroupRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;
import java.util.Optional;


@Service
public class ContributionsPlanService {
    @Autowired
    ContributionsPlanRepository repository;

    @Autowired
    PeopleGroupRepository peopleGroupRepository;
    public UniversalResponse saveContributionsPlan(ContributionsPlan account, Long parentId) {
        Optional<PeopleGroup> peopleGroup = peopleGroupRepository.findById(parentId);
        if (peopleGroup.isEmpty())
            return null;
        account.setPeopleGroup(peopleGroup.get());
        ContributionsPlan savedContributionsPlan = repository.save(account);
        System.out.println(savedContributionsPlan);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedContributionsPlan);
        response.setStatusCode(201);
        return response;
    }

    public UniversalResponse updateContributionsPlan(ContributionsPlan account, Long parentId) {
        Optional<PeopleGroup> peopleGroup = peopleGroupRepository.findById(parentId);
        if (peopleGroup.isEmpty())
            return null;
        Optional<ContributionsPlan> accountOptional = repository.findById(account.getId());
        if (accountOptional.isEmpty()) {
            return null;
        }

        if (!Objects.equals(accountOptional.get().getPeopleGroup().getId(), parentId)) return null;

        accountOptional.get().update(account);

        ContributionsPlan updatedContributionsPlan = repository.save(accountOptional.get());

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedContributionsPlan);
        response.setStatusCode(201);
        return response;
    }

    public String[] getTargetType() {
        String[] targetTypes = new String[]{"anyhow", "pledge", "weekly", "monthly", "annual"};
        return targetTypes;
    }
}
