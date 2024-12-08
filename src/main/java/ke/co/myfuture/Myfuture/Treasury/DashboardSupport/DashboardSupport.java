package ke.co.myfuture.Myfuture.Treasury.DashboardSupport;


import ke.co.myfuture.Myfuture.Treasury.Account.AccountRepository;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlanRepository;
import org.apache.poi.hpsf.Decimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DashboardSupport {
    @Autowired
    public ContributionsPlanRepository contributionsPlanRepository;

    @Autowired
    public AccountRepository accountRepository;

    public Optional<Snapshot> getSnapshotForGroup(Long groupId) {
        return accountRepository.getSnapshotForGroup(groupId);
    }

    public Optional<Snapshot> getSnapshotForPlan(Long planId) {
        return accountRepository.getSnapshotForPlan(planId);
    }

    public interface Snapshot{
        Long getId();
        Double getTotalCashAndEquivalents();
        Double getTotalPledges();
        Double getTotalIncome();
        Double getTotalExpenses();
    }
}

