package ke.co.myfuture.Myfuture.Treasury.Loan;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountStatus;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountOwnershipType;
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
import java.util.Date;

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
    private Double requestedAmount;

    @NotNull
    @Min(1)
    private Integer requestedDurationMonths;

    private String loanPurpose;

    // Approval and Disbursement
    private Double approvedAmount;
    private Date approvalDate;
    private String approvedBy;

    private Date disbursementDate;
    private String disbursedBy;

    // Repayment Tracking
    private Double totalRepaid = 0.0;
    private Double outstandingBalance;

    private Integer numberOfRepaymentsMade = 0;
    private Date nextDueDate;

    private Integer gracePeriodUsedDays = 0;
    private boolean fullyRepaid = false;

    @Enumerated(EnumType.STRING)
    private LoanStatus status = LoanStatus.PENDING;

    private Date applicationDate = new Date();
    private Date closedDate;

    // NEW: Last interest booking timestamp
    private Date lastInterestBookingDate;

    // NEW: Separate tracking for principal and interest paid
    private BigDecimal totalPrincipalPaid = BigDecimal.ZERO;
    private BigDecimal totalInterestPaid = BigDecimal.ZERO;

    @NotNull
    private Boolean disburseThroughSavings;
    @ManyToOne
    private Account disbursementAccount;


    @ManyToOne(optional = false)
    private Account account;

    @NotNull
    private Boolean backdate;
    private String reasonForBackdating;
    private Date backdatedDisbursementDate;



    Date updatedAt;

//    @CreationTimestamp

    @Column(updatable = false)
    Date createdAt;

    Date deletedAt;

    Boolean deletedFlag = false;

    @Column(nullable = false)
    String createdBy;

    public void delete() {
        this.deletedAt = new Date();
        this.deletedFlag = true;
    }

    @PrePersist
    public void prePersist() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
        this.createdBy = UserRequestContext.getCurrentUserName();
        if (UserRequestContext.getCurrentUserName() == null)
            this.createdBy = "Internal";
        validate();
    }

    @PreUpdate
    public void preUpdate() {
        validate();
    }

    public boolean createAccount(ContributionsPlan contributionsPlan, PeopleGroup peopleGroup) {
        if (peopleGroup.getId() == null){
            System.out.println("People group id is null");
            return false;
        }
        if (contributionsPlan.getId() == null){
            System.out.println("Plan id is null");
            return false;
        }
        Account newAccount = new Account();
        newAccount.setContributionsPlan(contributionsPlan);
        newAccount.setPeopleGroup(peopleGroup);
        newAccount.setName(loanProduct.getName() + " - " + borrower.getName() );
        newAccount.setOwner(this.borrower);
        newAccount.setOwnershipType(AccountOwnershipType.LOAN_RECEIVABLE); // assuming this enum exists
        newAccount.setProductCode(loanProduct.getProductCode());
        newAccount.setProductName(loanProduct.getName());
        newAccount.setTargetAmount(this.requestedAmount.doubleValue());
        newAccount.setStartDate(this.applicationDate);
        newAccount.setStatus(AccountStatus.PENDING);

        // createdBy will be set in @PrePersist
        this.account = newAccount;

        return true;
    }

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