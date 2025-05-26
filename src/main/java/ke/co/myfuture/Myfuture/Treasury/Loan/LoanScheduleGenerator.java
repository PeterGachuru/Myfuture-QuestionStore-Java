package ke.co.myfuture.Myfuture.Treasury.Loan;

import ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct.InterestRateType;
import ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct.LoanProduct;
import ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct.LoanProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoanScheduleGenerator {
    @Autowired
    LoanProductRepository loanProductRepository;

    public List<LoanScheduleItem> generateSchedule(CreateLoanRequest request) {
        return generateSchedule(request, loanProductRepository.findById(request.getLoanProductId()).get());
    }

    public List<LoanScheduleItem> generateSchedule(CreateLoanRequest request, LoanProduct product) {
        List<LoanScheduleItem> schedule = new ArrayList<>();

        BigDecimal loanAmount = request.getRequestedAmount();
        int duration = request.getRequestedDurationMonths();
        BigDecimal rate = product.getInterestRate().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        LocalDate disbursementDate = request.getBackdate()? request.getBackdatedDisbursementDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate(): LocalDate.now();

        LocalDate startDate = disbursementDate.plusDays(
                Optional.ofNullable(product.getGracePeriodDays()).orElse(0)
        );

        if (product.getInterestRateType() == InterestRateType.FLAT_RATE) {
            BigDecimal totalInterest = loanAmount.multiply(rate).multiply(BigDecimal.valueOf(duration));
            BigDecimal totalPayment = loanAmount.add(totalInterest);
            BigDecimal monthlyPayment = totalPayment.divide(BigDecimal.valueOf(duration), 2, RoundingMode.HALF_UP);
            BigDecimal monthlyPrincipal = loanAmount.divide(BigDecimal.valueOf(duration), 2, RoundingMode.HALF_UP);
            BigDecimal monthlyInterest = totalInterest.divide(BigDecimal.valueOf(duration), 2, RoundingMode.HALF_UP);
            BigDecimal remaining = loanAmount;

            for (int i = 1; i <= duration; i++) {
                LocalDate dueDate = startDate.plusMonths(i - 1);
                remaining = remaining.subtract(monthlyPrincipal);

                schedule.add(new LoanScheduleItem(
                        i,
                        dueDate,
                        monthlyPrincipal,
                        monthlyInterest,
                        monthlyPayment,
                        remaining.max(BigDecimal.ZERO)
                ));
            }

        } else if (product.getInterestRateType() == InterestRateType.REDUCING_BALANCE) {
            BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
            BigDecimal remaining = loanAmount;

            // EMI formula: [P x R x (1+R)^N] / [(1+R)^N – 1]
            BigDecimal onePlusRPowerN = (BigDecimal.ONE.add(monthlyRate)).pow(duration);
            BigDecimal emi = loanAmount.multiply(monthlyRate).multiply(onePlusRPowerN)
                    .divide(onePlusRPowerN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

            for (int i = 1; i <= duration; i++) {
                BigDecimal interest = remaining.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal principal = emi.subtract(interest).setScale(2, RoundingMode.HALF_UP);
                remaining = remaining.subtract(principal).setScale(2, RoundingMode.HALF_UP);

                LocalDate dueDate = startDate.plusMonths(i - 1);

                schedule.add(new LoanScheduleItem(
                        i,
                        dueDate,
                        principal,
                        interest,
                        emi,
                        remaining.max(BigDecimal.ZERO)
                ));
            }
        }

        return schedule;
    }
}
