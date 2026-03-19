package ke.co.myfuture.Myfuture.Treasury.Loan;

import ke.co.myfuture.Myfuture.Treasury.Demands.Demand;
import ke.co.myfuture.Myfuture.Treasury.Demands.DemandBreakdown.DemandBreakdown;
import ke.co.myfuture.Myfuture.Treasury.Demands.DemandBreakdown.DemandComponentType;
import ke.co.myfuture.Myfuture.Treasury.Demands.DemandRepository;
import ke.co.myfuture.Myfuture.Treasury.Demands.DemandType;
import ke.co.myfuture.Myfuture.Treasury.Loan.LoanScheduleItem.LoanScheduleItem;
import ke.co.myfuture.Myfuture.Treasury.Loan.LoanScheduleItem.LoanScheduleItemRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanRepaymentService {
    private final LoanRepository loanRepository;
    private final LoanScheduleItemRepository scheduleRepository;
    private final DemandRepository demandRepository;
    private final LoanInterestService loanInterestService;

//    @Scheduled(cron = "0 0 6,12,18 * * ?") // Runs at 6AM, 12PM, 6PM
    @Scheduled(fixedRate = 300000)
    public void autoCreateLoanDemands() {
        generateDemandsForDueLoans();
    }


    /**
     * Automatically run every few hours (scheduled externally or via Spring @Scheduled).
     * This method checks for due/overdue loans and creates demands accordingly.
     */
    public void generateDemandsForDueLoans() {
        Date today = new Date();

        List<Loan> dueLoans = loanRepository.findByNextDueDateBeforeAndFullyRepaidFalse(today);

        System.out.println("Number of loans due: "+dueLoans.size());

        for (Loan loan : dueLoans) {
            Optional<LoanScheduleItem> scheduleItem = scheduleRepository
                    .findFirstByLoanAndDueDateAndInstallmentNumberNotNullOrderByInstallmentNumberAsc(loan, loan.getNextDueDate());

            if (scheduleItem.isPresent() && !hasExistingDemand(scheduleItem.get())) {
                UniversalResponse universalResponse = loanInterestService.bookInterestNow(loan, scheduleItem.get().getDueDate());

                if (universalResponse.getStatusCode() < 400) {
                    Optional<LoanScheduleItem> reloadedScheduledItem = scheduleRepository.findById(scheduleItem.get().getId());
                    generateDemandsForDueLoans(reloadedScheduledItem.get());
                }
            }
        }
    }

    @Transactional
    private void generateDemandsForDueLoans(LoanScheduleItem loanScheduleItem) {
        createDemandFromScheduleItem(loanScheduleItem);
        updateLoanNextDueDate(loanScheduleItem.getLoan());
    }
    private void updateLoanNextDueDate(Loan loan) {
        List<LoanScheduleItem> futureItems = scheduleRepository
                .findByLoanAndDueDateAfterOrderByDueDateAsc(loan, loan.getNextDueDate());

        if (!futureItems.isEmpty()) {
            loan.setNextDueDate(futureItems.get(0).getDueDate());
        } else {
            loan.setFullyRepaid(true); // Or handle gracefully
        }

        loanRepository.save(loan);
    }

    /**
     * Checks if a demand for a given schedule item already exists
     */
    private boolean hasExistingDemand(LoanScheduleItem item) {
        String ref = generateDemandReference(item);
        return demandRepository.existsByReference(ref);
    }

    /**
     * Creates a new demand for the given schedule item.
     */
    public void createDemandFromScheduleItem(LoanScheduleItem item) {
        Loan loan = item.getLoan();

        Double interestAmount = item.getLoan().getTotalInterestNotYetDemanded().doubleValue();
        Double principal = item.getPrincipal();
        Double totalToDemand = principal+interestAmount;

        Demand demand = new Demand();
        demand.setDemandType(DemandType.LOAN_REPAYMENT);
        demand.setReference(generateDemandReference(item));
        demand.setObligatedMember(loan.getBorrower());
        demand.setAccountToDebit(loan.getRepaymentAccount());
        demand.setAccountToCredit(loan.getAccount());
        demand.setAccountToDebit(loan.getDisbursementAccount()); // Optional
        demand.setDueDate(item.getDueDate());
        demand.setPreGenerated(false); // This is a real, active demand
        demand.setTotalAmount(totalToDemand);

        // Breakdown: principal
        if (item.getPrincipal() > 0) {
            DemandBreakdown principalBreakdown = new DemandBreakdown();
            principalBreakdown.setDemand(demand);
            principalBreakdown.setComponentType(DemandComponentType.PRINCIPAL);
            principalBreakdown.setAccountToCredit(loan.getAccount());
            principalBreakdown.setAmount(item.getPrincipal());
            demand.getBreakdowns().add(principalBreakdown);
        }

        // Breakdown: interest
        if (interestAmount > 0) {
            DemandBreakdown interestBreakdown = new DemandBreakdown();
            interestBreakdown.setDemand(demand);
            interestBreakdown.setComponentType(DemandComponentType.INTEREST);
            interestBreakdown.setAccountToCredit(loan.getAccount());
            interestBreakdown.setAmount(interestAmount);
            demand.getBreakdowns().add(interestBreakdown);
        }
        String remarks = demand.getBreakdowns().stream()
                .map(b -> b.getComponentType().name() + ": " + b.getAmount())
                .collect(Collectors.joining(", "));
        demand.setRemarks(remarks);
        Demand savedDemand = demandRepository.save(demand);

        // Optionally update the loan’s demand tracking
        loan.setLastInterestDemandDate(new Date());
        loan.setTotalInterestNotYetDemanded(BigDecimal.ZERO);
        loanRepository.save(loan);

        item.setDemanded(savedDemand);
        scheduleRepository.save(item);
    }

    /**
     * Generates a reference like "LOAN2025-0001-SCH-3"
     */
    private String generateDemandReference(LoanScheduleItem item) {
        return "LOAN" + item.getLoan().getId() + "-SCH-" + item.getInstallmentNumber();
    }
}
