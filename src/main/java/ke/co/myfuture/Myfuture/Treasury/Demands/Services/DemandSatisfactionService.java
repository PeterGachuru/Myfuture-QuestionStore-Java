package ke.co.myfuture.Myfuture.Treasury.Demands.Services;


import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountService;
import ke.co.myfuture.Myfuture.Treasury.Demands.DemandPayment.DemandPaymentRepository;
import ke.co.myfuture.Myfuture.Treasury.Demands.DemandRepository;
import ke.co.myfuture.Myfuture.Treasury.Loan.Loan;
import ke.co.myfuture.Myfuture.Treasury.Loan.LoanRepository;
import ke.co.myfuture.Myfuture.Treasury.Loan.LoanStatus;
import ke.co.myfuture.Myfuture.Treasury.Transaction.Transaction;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TransactionCategory;
import ke.co.myfuture.Myfuture.Treasury.Demands.Demand;
import ke.co.myfuture.Myfuture.Treasury.Demands.DemandPayment.DemandPayment;
import ke.co.myfuture.Myfuture.Treasury.Transaction.SystemTransactionService;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TransactionBuilder;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DemandSatisfactionService {
    @Autowired
    SystemTransactionService systemTransactionService;
    @Autowired
    LoanRepository loanRepository;
    @Autowired
    DemandRepository demandRepository;
    @Autowired
    DemandPaymentRepository demandPaymentRepository;
    @Autowired
    AccountService accountService;

    @Scheduled(fixedDelay = 190000) // runs 20 seconds after previous execution completes
    public void runUnsatisfiedDemandProcessor() {
        List<Demand> unsatisfiedDemands = demandRepository
                .findBySettledFalseAndDeletedFlagFalseAndDueDateBefore(new Date());
        System.out.println("count of unsatisfied demands: "+unsatisfiedDemands.size());

        for (Demand demand : unsatisfiedDemands) {
            try {
                satisfyDemand(demand);
            } catch (Exception e) {
                // Log error and continue with next demand
                System.err.println("Failed to satisfy demand ID: " + demand.getId() + ", reason: " + e.getMessage());
            }
        }
    }
    public void satisfyDemand(Demand demand) {
        if (demand.getSettled()) {
            throw new IllegalStateException("Demand already settled.");
        }

        switch (demand.getDemandType()) {
            case LOAN_REPAYMENT:
                satisfyLoanRepaymentDemand(demand.getId());
                break;
            // Add more types in future, e.g.
            // case MEMBERSHIP_FEE:
            //     satisfyMembershipFeeDemand(demand);
            //     break;
            default:
                throw new UnsupportedOperationException("Unsupported demand type: " + demand.getDemandType());
        }
    }

    @Transactional
    private void satisfyLoanRepaymentDemand(Long demandId) {
        Optional<Demand> demandOptional = demandRepository.findById(demandId);
        Demand demand = demandOptional.get();
        Account source = demand.getAccountToDebit();
        Account target = demand.getAccountToCredit();
        Double amount = demand.getTotalAmount();

        // Build the transaction
        TransactionBuilder builder = TransactionBuilder.builder()
                .transactionCategory(TransactionCategory.LOAN_REPAYMENT)
                .debitAccount(source)  // e.g., member's account
                .creditAccount(target) // e.g., loan account or income account
                .amount(amount)
                .oneOfTheAccounts(source)
                .debitParticulars("Loan repayment for demand " + demand.getReference())
                .creditParticulars("Received loan repayment " + demand.getReference())
                .contributionsPlan(source.getContributionsPlan())
                .build();

        UniversalResponse response = systemTransactionService.saveTransaction(builder);

        if (response.getStatusCode() >= 400) {
            return;
        }

        // Mark demand as settled
        demand.setSettled(true);
        // Save the updated demand
        demandRepository.save(demand);

        // Record payment
        DemandPayment payment = new DemandPayment();
        payment.setDemand(demand);
        payment.setAmountPaid(amount);
        payment.setTransaction((Transaction) response.getEntity());
        payment.setPaymentDate(new Date());

        demandPaymentRepository.save(payment);

        // Update the loan associated with the credit account (if applicable)
        Optional<Loan> loanOptional = loanRepository.findByAccount(target);
        if (loanOptional.isPresent()) {
            Loan loan = loanOptional.get();
            loan.setTotalRepaid(loan.getTotalRepaid() + amount);
            loan.setOutstandingBalance(loan.getOutstandingBalance() - amount);
            loan.setNumberOfRepaymentsMade(loan.getNumberOfRepaymentsMade() + 1);

            if (Math.abs(loan.getOutstandingBalance()) <= 0.05 && Math.abs(loan.getAccount().getBalance()) <= 0.05) {
                System.out.println("loan fully paid, closing account");
                loan.setFullyRepaid(true);
                loan.setStatus(LoanStatus.CLOSED);
                loan.setClosedDate(new Date());
                accountService.closeAccount(loan.getAccount(), "Loan repaid fully");
            }

            loanRepository.save(loan);
        }
    }
}
