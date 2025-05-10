package ke.co.myfuture.Myfuture.Treasury.Loan;

import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct.LoanProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Person who took the loan
    @ManyToOne(optional = false)
    private Person borrower;

    @NotNull
    private String name;

    // Optional: Group where the borrower belongs
    @ManyToOne()
    private PeopleGroup borrowerGroup;

    @ManyToOne(optional = false)
    private LoanProduct loanProduct;

    // Loan Request
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal requestedAmount;

    @NotNull
    @Min(1)
    private Integer requestedDurationMonths;

    private String loanPurpose;

    // Approval and Disbursement
    private BigDecimal approvedAmount;
    private LocalDateTime approvalDate;
    private String approvedBy;

    private LocalDateTime disbursementDate;
    private String disbursedBy;

    // Repayment Tracking
    private BigDecimal totalRepaid = BigDecimal.ZERO;
    private BigDecimal outstandingBalance;

    private Integer numberOfRepaymentsMade = 0;
    private LocalDateTime nextDueDate;

    private Integer gracePeriodUsedDays = 0;
    private boolean fullyRepaid = false;

    @Enumerated(EnumType.STRING)
    private LoanStatus status = LoanStatus.PENDING;

    private LocalDateTime applicationDate = LocalDateTime.now();
    private LocalDateTime closedDate;

    // NEW: Last interest booking timestamp
    private LocalDateTime lastInterestBookingDate;

    // NEW: Separate tracking for principal and interest paid
    private BigDecimal totalPrincipalPaid = BigDecimal.ZERO;
    private BigDecimal totalInterestPaid = BigDecimal.ZERO;

    // Validation
    @PrePersist
    @PreUpdate
    private void validate() {
        if (loanProduct != null) {
            if (requestedAmount.compareTo(loanProduct.getMinLoanAmount()) < 0 ||
                    requestedAmount.compareTo(loanProduct.getMaxLoanAmount()) > 0) {
                throw new IllegalArgumentException("Requested amount must be within the product limits.");
            }

            if (requestedDurationMonths < loanProduct.getMinDurationMonths() ||
                    requestedDurationMonths > loanProduct.getMaxDurationMonths()) {
                throw new IllegalArgumentException("Requested duration must be within product duration limits.");
            }
        }

        if (approvedAmount != null && outstandingBalance == null) {
            this.outstandingBalance = approvedAmount;
        }
    }
}