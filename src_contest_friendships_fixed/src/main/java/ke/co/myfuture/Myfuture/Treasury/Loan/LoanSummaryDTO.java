package ke.co.myfuture.Myfuture.Treasury.Loan;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;


@Data
@AllArgsConstructor
public class LoanSummaryDTO {

    private Long id;

    private String borrowerName;
    private String borrowerGroupName;

    private Double requestedAmount;
    private Integer requestedDurationMonths;
    private String loanPurpose;

    private Double approvedAmount;
    private String approvedBy;
    private Date approvalDate;

    private Double outstandingBalance;
    private Double totalRepaid;
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