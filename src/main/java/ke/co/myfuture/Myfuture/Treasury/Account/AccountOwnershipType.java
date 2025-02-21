package ke.co.myfuture.Myfuture.Treasury.Account;

public enum AccountOwnershipType {
    INCOME,
    CASH,
    EXPENSE,
    LOAN_RECEIVABLE,  // Money lent out (Asset)
    LOAN_PAYABLE      // Money borrowed (Liability)
}