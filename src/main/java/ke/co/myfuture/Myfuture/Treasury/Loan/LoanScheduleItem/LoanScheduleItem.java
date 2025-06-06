package ke.co.myfuture.Myfuture.Treasury.Loan.LoanScheduleItem;

import ke.co.myfuture.Myfuture.Treasury.Loan.Loan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class LoanScheduleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int installmentNumber;
    private Date dueDate;
    private Double principal;
    private Double interest;
    private Double totalPayment;
    private Double remainingBalance;
    @ManyToOne(optional = false)
    private Loan loan;

    public LoanScheduleItem(int installmentNumber, Date dueDate, Double principal,
                            Double interest, Double totalPayment,
                            Double remainingBalance) {
        this.installmentNumber = installmentNumber;
        this.dueDate = dueDate;
        this.principal = principal;
        this.interest = interest;
        this.totalPayment = totalPayment;
        this.remainingBalance = remainingBalance;
    }
}