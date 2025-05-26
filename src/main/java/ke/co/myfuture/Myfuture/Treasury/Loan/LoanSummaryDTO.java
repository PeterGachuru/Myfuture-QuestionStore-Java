package ke.co.myfuture.Myfuture.Treasury.Loan;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
@AllArgsConstructor
public class LoanSummaryDTO {

    private Long id;

    private String borrowerName;
    private String borrowerGroupName;

    private BigDecimal requestedAmount;
    private Integer requestedDurationMonths;
    private String loanPurpose;

    private BigDecimal approvedAmount;
    private String approvedBy;
    private Date approvalDate;

    private BigDecimal outstandingBalance;
    private BigDecimal totalRepaid;
    private Integer numberOfRepaymentsMade;

    private Date disbursementDate;
    private String disbursedBy;

    private Date nextDueDate;

    private Integer gracePeriodUsedDays;
    private boolean fullyRepaid;

    private LoanStatus status;
    private Date applicationDate;
    private Date closedDate;
}