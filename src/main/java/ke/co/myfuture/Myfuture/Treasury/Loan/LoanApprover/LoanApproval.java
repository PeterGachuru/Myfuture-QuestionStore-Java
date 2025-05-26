package ke.co.myfuture.Myfuture.Treasury.Loan.LoanApprover;


import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.Loan.Loan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Person who took the loan
    @ManyToOne(optional = false)
    private Loan loan;


    @Column(nullable = false)
    ApprovalStatus approvalStatus;

    @Column(nullable = false)
    Boolean usable;

    @Column
    String approver;

    @Column(updatable = false)
    Date createdAt;

    @Column(nullable = false)
    String createdBy;

    @PrePersist
    public void prePersist() {
        Date now = new Date();
        this.createdAt = now;
        this.usable = true;
        this.approver = UserRequestContext.getCurrentUserName();
        this.createdBy = UserRequestContext.getCurrentUserName();
    }
}
