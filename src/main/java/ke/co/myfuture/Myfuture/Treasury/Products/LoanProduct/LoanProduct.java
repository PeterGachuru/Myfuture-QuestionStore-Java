package ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.Products.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"productCode", "people_group_id"}))
public class LoanProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, max = 255)
    private String name;

    @NotNull
    private String productCode;

    @NotNull
    @DecimalMin("0.0")
    private Double interestRate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InterestRateType interestRateType;

    @ManyToOne
    private Account interestIncomeAccount;

    @NotNull
    @Min(1)
    private Integer minDurationMonths;

    @NotNull
    @Min(1)
    private Integer maxDurationMonths;
    @NotNull
    @Min(1)
    private Integer numberOfApproversRequired;

    @NotNull
    @DecimalMin("0.0")
    private Double maxLoanAmount;

    @NotNull
    @DecimalMin("0.0")
    private Double minLoanAmount;

    private String loanPurpose;

    private Integer gracePeriodDays;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    private String description;


    @ManyToOne(optional = false)
    @JsonIgnore
    private PeopleGroup peopleGroup;
    @ManyToOne(optional = false)
    @JsonIgnore
    private ContributionsPlan contributionsPlan;

    Date updatedAt;
    String updatedBy;

//    @CreationTimestamp

    @Column(updatable = false, nullable = false)
    Date createdAt;

    Date approvedAt;
    String approvedBy;

    Date rejectedAt;
    String rejectedBy;

    Date deletedAt;

    Boolean deletedFlag = false;

    String deletedBy;

    @Column(nullable = false)
    String createdBy;

    public void delete() {
        this.deletedAt = new Date();
        this.deletedFlag = true;
        this.deletedBy = UserRequestContext.getCurrentUserName();
        if (UserRequestContext.getCurrentUserName() == null)
            this.deletedBy = "Internal";
    }

    @PrePersist
    public void prePersist() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
        this.createdBy = UserRequestContext.getCurrentUserName();
        if (UserRequestContext.getCurrentUserName() == null)
            this.createdBy = "Internal";
    }
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
        this.updatedBy = UserRequestContext.getCurrentUserName();
        if (UserRequestContext.getCurrentUserName() == null)
            this.updatedBy = "Internal";
    }

    public void setName(String name) {
        this.name = name;
        this.productCode = generateProductCode(name);
    }

    private String generateProductCode(String name) {
        if (name != null && name.length() > 1) {
            return name.substring(0, 2).toUpperCase();
        }
        return "NP";
    }

    public void approve() {
        status =  ProductStatus.ACTIVE;
        approvedAt = new Date();
        approvedBy =  UserRequestContext.getCurrentUserName();
        if (UserRequestContext.getCurrentUserName() == null)
            this.approvedBy = "Internal";
    }

    public void reject() {
        status =  ProductStatus.REJECTED;
        rejectedAt = new Date();
        rejectedBy =  UserRequestContext.getCurrentUserName();
        if (UserRequestContext.getCurrentUserName() == null)
            this.rejectedBy = "Internal";
    }
}