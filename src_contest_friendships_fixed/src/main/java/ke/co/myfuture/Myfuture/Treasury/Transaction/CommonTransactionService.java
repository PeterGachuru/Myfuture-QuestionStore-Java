package ke.co.myfuture.Myfuture.Treasury.Transaction;


import ke.co.myfuture.Myfuture.Treasury.Transaction.TranValidators.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonTransactionService {
    @Autowired
    BorrowValidator borrowValidator;
    @Autowired
    ExpenseValidator expenseValidator;
    @Autowired
    LendValidator lendValidator;
    @Autowired
    RepayBorrowValidator repayBorrowValidator;
    @Autowired
    RepayLendValidator repayLendValidator;
    @Autowired
    SavingDepositValidator savingDepositValidator;
    @Autowired
    SavingWithdrawalValidator savingWithdrawalValidator;
    @Autowired
    IncomeValidator incomeValidator;
    @Autowired
    LoanDisbursementValidator loanDisbursementValidator;
    @Autowired
    ReturnExpenseCashValidator returnExpenseCashValidator;
    public boolean attachOtherTranEntries(Transaction transaction) {
        System.out.println("----attachOtherTranEntries----");
        if (transaction.getCategory() == TransactionCategory.EXPENSE) {
            return expenseValidator.attachOtherTranEntries(transaction);
        } else if (transaction.getCategory() == TransactionCategory.INCOME) {
            return incomeValidator.attachOtherTranEntries(transaction);
        } else  if (transaction.getCategory() == TransactionCategory.LEND) {
            return lendValidator.attachOtherTranEntries(transaction);
        } else  if (transaction.getCategory() == TransactionCategory.REPAY_LEND) {
            return repayLendValidator.attachOtherTranEntries(transaction);
        } else if (transaction.getCategory() == TransactionCategory.BORROW) {
            return borrowValidator.attachOtherTranEntries(transaction);
        } else if (transaction.getCategory() == TransactionCategory.REPAY_BORROW) {
            return repayBorrowValidator.attachOtherTranEntries(transaction);
        } else if (transaction.getCategory() == TransactionCategory.SAVING_DEPOSIT) {
            return savingDepositValidator.attachOtherTranEntries(transaction);
        } else if (transaction.getCategory() == TransactionCategory.SAVING_WITHDRAWAL) {
            return savingWithdrawalValidator.attachOtherTranEntries(transaction);
        }else if (transaction.getCategory() == TransactionCategory.LOAN_DISBURSEMENT_BY_CASH) {
            return loanDisbursementValidator.attachOtherTranEntries(transaction);
        }else if (transaction.getCategory() == TransactionCategory.RETURN_EXPENSE_CASH) {
            return returnExpenseCashValidator.attachOtherTranEntries(transaction);
        }
        return true;
    }
}
