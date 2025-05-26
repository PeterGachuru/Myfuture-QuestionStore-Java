package ke.co.myfuture.Myfuture.Treasury.Loan.LoanApprover;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanApprovalRepository extends JpaRepository<LoanApproval, Long> {
    @Modifying
    @Query("UPDATE LoanApproval la SET la.usable = false WHERE la.loan.id = :loanId AND la.approver = :approver AND la.usable = true")
    void disableAllForApproverAndLoan(@Param("loanId") Long loanId, @Param("approver") String approver);

    @Query("SELECT COUNT(la) FROM LoanApproval la WHERE la.loan.id = :loanId AND la.usable = true AND la.approvalStatus = 'APPROVED'")
    int findApprovalsCount(@Param("loanId") Long loanId);

    @Query("SELECT CASE WHEN COUNT(la) > 0 THEN true ELSE false END FROM LoanApproval la WHERE la.loan.id = :loanId AND la.usable = true AND la.approvalStatus = 'REJECTED'")
    Boolean hasReject(@Param("loanId") Long loanId);

}
