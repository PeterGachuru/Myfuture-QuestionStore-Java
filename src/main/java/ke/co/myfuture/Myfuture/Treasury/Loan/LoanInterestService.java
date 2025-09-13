package ke.co.myfuture.Myfuture.Treasury.Loan;


import ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct.InterestRateType;
import ke.co.myfuture.Myfuture.Treasury.Transaction.SystemTransactionService;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TransactionBuilder;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TransactionCategory;
import ke.co.myfuture.Myfuture.Utils.DateUtils;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class LoanInterestService {
    private final SystemTransactionService systemTransactionService;
    private final LoanRepository loanRepository;

    public UniversalResponse bookInterestNow(Loan loan,  Date today) {
        Date interestStartDate = loan.getLastInterestBookingDate();

        if (interestStartDate == null) {
            interestStartDate = loan.getDisbursementDate();
        }

        if (interestStartDate == null || loan.getApprovedAmount() == null || loan.getLoanProduct() == null) {
            return new UniversalResponse(400, null, "Cannot book interest: missing loan data");
        }

        double interest = calculateAccruedInterest(
                loan.getApprovedAmount(),
                loan.getLoanProduct().getInterestRate(),
                loan.getLoanProduct().getInterestRateType(),
                interestStartDate,
                today,
                loan.getOutstandingBalance() != null ? loan.getOutstandingBalance() : loan.getApprovedAmount()
        );

        if (interest <= 0) {
            return new UniversalResponse(200, null, "No interest to book as of today");
        }

        TransactionBuilder builder = TransactionBuilder.builder()
                .transactionCategory(TransactionCategory.LOAN_INTEREST_BOOKING)
                .debitAccount(loan.getAccount()) // borrower's account is debited
                .creditAccount(loan.getLoanProduct().getInterestIncomeAccount()) // income account credited
                .amount(interest)
                .oneOfTheAccounts(loan.getAccount())
                .debitParticulars("Interest booking "+DateUtils.formatDate(today, "yyyy-MMM-dd"))
                .creditParticulars("Interest earned "+DateUtils.formatDate(today, "yyyy-MMM-dd"))
                .contributionsPlan(loan.getAccount().getContributionsPlan())
                .build();

        UniversalResponse response = systemTransactionService.saveTransaction(builder);

        if (response.getStatusCode() < 400) {
            loan.setLastInterestBookingDate(today);
            if (loan.getTotalInterestBooked() == null) {
                loan.setTotalInterestBooked(BigDecimal.ZERO);
            }
            if (loan.getTotalInterestNotYetDemanded() == null){
                loan.setTotalInterestNotYetDemanded(BigDecimal.ZERO);
            }
            loan.setTotalInterestBooked(
                    loan.getTotalInterestBooked().add(BigDecimal.valueOf(interest))
            );
            loan.setOutstandingBalance(
                    loan.getOutstandingBalance()+BigDecimal.valueOf(interest).doubleValue()
            );
            loan.setTotalInterestNotYetDemanded(
                    loan.getTotalInterestNotYetDemanded().add(BigDecimal.valueOf(interest))
            );
            loanRepository.save(loan);
        }

        return response;
    }

    public double calculateAccruedInterest(
            double loanAmount,
            double annualInterestRatePercent,
            InterestRateType interestRateType,
            Date lastInterestCalculationDate,
            Date currentDate,
            double remainingPrincipal // relevant for REDUCING_BALANCE
    ){
        return calculateAccruedInterest(loanAmount, annualInterestRatePercent,
                interestRateType,
                DateUtils.convertDateToLocalDate(lastInterestCalculationDate),
                DateUtils.convertDateToLocalDate(currentDate),
                remainingPrincipal);
    }

    public double calculateAccruedInterest(
            double loanAmount,
            double annualInterestRatePercent,
            InterestRateType interestRateType,
            LocalDate lastInterestCalculationDate,
            LocalDate currentDate,
            double remainingPrincipal // relevant for REDUCING_BALANCE
    ) {
        if (currentDate.isBefore(lastInterestCalculationDate)) {
            System.out.println("Current: "+currentDate);
            System.out.println("lastInterestCalculationDate: "+lastInterestCalculationDate);
            throw new IllegalArgumentException("Current date cannot be before last interest calculation date.");
        }

        double annualRate = annualInterestRatePercent / 100.0;
        long daysBetween = ChronoUnit.DAYS.between(lastInterestCalculationDate, currentDate);

        switch (interestRateType) {
            case FLAT_RATE:
                // Flat rate: interest is calculated on original principal regardless of balance
                return Math.round((loanAmount * annualRate * daysBetween / 365.0) * 100.0) / 100.0;

            case REDUCING_BALANCE:
                // Reducing balance: interest calculated on remaining balance
                System.out.println("Calculating for reducing balance");
                System.out.println("remainingPrincipal: "+remainingPrincipal+", annualRate: "+annualRate+", daysBetween: "+daysBetween);
                System.out.println("interest: "+Math.round((remainingPrincipal * annualRate * daysBetween / 365.0) * 100.0) / 100.0);
                return Math.round((remainingPrincipal * annualRate * daysBetween / 365.0) * 100.0) / 100.0;

            default:
                throw new UnsupportedOperationException("Unsupported interest rate type.");
        }
    }
}
