package ke.co.myfuture.Myfuture.Treasury.Transaction;

import java.util.Date;

public interface TransactionDTO {
    Long getId();

    Double getAmountInvolved();

    String getStatus();


    String getExternalTransactionCode();

    String getExternalTransactionMessage();

    String getHolderParticulars();

    String getOneOfTheAccounts();

    Long getContributionsPlanId();

    TransactionCategory getCategory();

//    Long getPlanId();

    Boolean getReversal();

    Long getReversalFor();

    Date getUpdatedAt();

    Date getCreatedAt();

    Date getDeletedAt();

    Boolean getDeletedFlag();

    String getCreatedBy();
}
