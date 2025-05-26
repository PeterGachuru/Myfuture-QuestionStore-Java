package ke.co.myfuture.Myfuture.Treasury.Loan.LoanApprover;


import lombok.Data;

@Data
public class LoanApproveDTO {
    private Long loanId;
    private ApprovalStatus approvalStatus;
}
