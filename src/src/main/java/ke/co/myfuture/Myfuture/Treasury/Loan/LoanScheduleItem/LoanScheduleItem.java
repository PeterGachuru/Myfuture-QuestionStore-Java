package ke.co.myfuture.Myfuture.Treasury.Loan.LoanScheduleItem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ke.co.myfuture.Myfuture.Treasury.Demands.Demand;
import ke.co.myfuture.Myfuture.Treasury.Loan.Loan;
import lombok.*;

import jakarta.persistence.*;
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
    @Column(nullable = false)
    private Date dueDate;
    @Column(nullable = false)
    private Double principal;
    @Column(nullable = false)
    private Double interest;
    @Column(nullable = false)
    private Double totalPayment;
    private Double remainingBalance;
    @ManyToOne(optional = false)
    @JsonIgnore
    private Loan loan;
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Demand demand;

    @Setter(AccessLevel.NONE)
    private Boolean demanded = false;
    private Date dateDemanded;

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

    public void setDemanded(Demand savedDemand) {
        demanded = true;
        dateDemanded = new Date();
        demand = savedDemand;
    }
}