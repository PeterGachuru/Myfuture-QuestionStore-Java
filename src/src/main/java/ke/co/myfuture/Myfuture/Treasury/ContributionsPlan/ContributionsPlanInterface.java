package ke.co.myfuture.Myfuture.Treasury.ContributionsPlan;

import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;

import java.util.Date;
import java.util.List;

public interface ContributionsPlanInterface {
    Long getId();
    Date getStartDate();
    Date getDeadlineDate();
    Date getClosureDate();
    String getTargetType();
    Double getTargetAmount();
    String getName();
    String getNotes();
    Double getIndividualContributorTarget();
    Integer getPinPriority();
    Date getUpdatedAt();
    Date getCreatedAt();
    Date getDeletedAt();
    Boolean getDeletedFlag();
    String getCreatedBy();
    Double getAvailableCash();
}
