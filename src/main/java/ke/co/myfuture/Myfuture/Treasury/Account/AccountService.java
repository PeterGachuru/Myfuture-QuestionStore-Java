package ke.co.myfuture.Myfuture.Treasury.Account;

import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlanRepository;
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
    ContributionsPlanRepository contributionsPlanRepository;

    public UniversalResponse saveAccount(Account account) {
        Optional<ContributionsPlan> contributionsPlan = contributionsPlanRepository.findById(account.getPlanId());
        if (contributionsPlan.isEmpty()) return null;
        account.setContributionsPlan(contributionsPlan.get());
        Account savedAccount = repository.save(account);
        System.out.println(savedAccount);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedAccount);
        response.setStatusCode(201);
        return response;
    }
}
