package ke.co.myfuture.Myfuture.Treasury.Loan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanScheduleItem {
    private int installmentNumber;
    private LocalDate dueDate;
    private Double principal;
    private Double interest;
    private Double totalPayment;
    private Double remainingBalance;
}
