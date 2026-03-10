package ke.co.myfuture.Myfuture.Treasury.ContributionsPlan;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class ContributionsPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private Date startDate;
    private Date deadlineDate;
    private Date closureDate;
    String targetType = "anyhow"; //pledge//anyhow//weekly//monthly//annual
    Double targetAmount = 0.0;
    String name;
    String notes;
    Double individualContributorTarget = 0.0;
    Integer pinPriority = 1;
    @ManyToOne
    PeopleGroup peopleGroup;
    @OneToOne
    Account expenseAccount;
//    @OneToMany
    @Transient
    List<Account> moneyInAccounts;

    public void update(ContributionsPlan account) {
        this.name = account.name;
        this.startDate = account.startDate;
        this.notes = account.notes;
        this.targetType = account.targetType;
        this.individualContributorTarget = account.individualContributorTarget;
        this.deadlineDate = account.deadlineDate;
        this.closureDate = account.closureDate;
        this.targetAmount = account.targetAmount;
        this.pinPriority = account.pinPriority;
    }

    /**
     * AuditTrails
     */

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
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    static public interface Retriever {
        String getUpdatedAt();
        String getCreatedAt();

        String getCreatedBy();

        Boolean getDeletedFlag();
    }
}
