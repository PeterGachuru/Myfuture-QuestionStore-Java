package ke.co.myfuture.Myfuture.Treasury.Loan;

import ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct.InterestRateType;
import ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct.LoanProduct;
import ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct.LoanProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        double loanAmount = request.getRequestedAmount().doubleValue();
        int duration = request.getRequestedDurationMonths();
        double rate = product.getInterestRate().doubleValue() / 100.0;

        LocalDate disbursementDate = request.getBackdate()
                ? request.getBackdatedDisbursementDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : LocalDate.now();

        LocalDate startDate = disbursementDate.plusDays(
                Optional.ofNullable(product.getGracePeriodDays()).orElse(0)
        );

        if (product.getInterestRateType() == InterestRateType.FLAT_RATE) {
            double totalInterest = loanAmount * rate * duration;
            double totalPayment = loanAmount + totalInterest;
            double monthlyPayment = Math.round(totalPayment / duration * 100.0) / 100.0;
            double monthlyPrincipal = Math.round(loanAmount / duration * 100.0) / 100.0;
            double monthlyInterest = Math.round(totalInterest / duration * 100.0) / 100.0;
            double remaining = loanAmount;

            for (int i = 1; i <= duration; i++) {
                LocalDate dueDate = startDate.plusMonths(i - 1);
                remaining = Math.max(0.0, remaining - monthlyPrincipal);

                schedule.add(new LoanScheduleItem(
                        i,
                        dueDate,
                        monthlyPrincipal,
                        monthlyInterest,
                        monthlyPayment,
                        remaining
                ));
            }

        } else if (product.getInterestRateType() == InterestRateType.REDUCING_BALANCE) {
            double monthlyRate = rate / 12.0;
            double remaining = loanAmount;

            double onePlusRPowerN = Math.pow(1 + monthlyRate, duration);
            double emi = Math.round((loanAmount * monthlyRate * onePlusRPowerN) / (onePlusRPowerN - 1) * 100.0) / 100.0;

            for (int i = 1; i <= duration; i++) {
                double interest = Math.round(remaining * monthlyRate * 100.0) / 100.0;
                double principal = Math.round((emi - interest) * 100.0) / 100.0;
                remaining = Math.max(0.0, Math.round((remaining - principal) * 100.0) / 100.0);

                LocalDate dueDate = startDate.plusMonths(i - 1);

                schedule.add(new LoanScheduleItem(
                        i,
                        dueDate,
                        principal,
                        interest,
                        emi,
                        remaining
                ));
            }
        }

        return schedule;
    }
}
