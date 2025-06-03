package ke.co.myfuture.Myfuture.Treasury.Transaction;

import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class TransactionBuilder {
    private Account creditAccount;
    private Account debitAccount;
    private Account oneOfTheAccounts;
    private Double amount;
    private String debitParticulars;
    private String creditParticulars;
    private TransactionCategory transactionCategory;
    private ContributionsPlan contributionsPlan;
}