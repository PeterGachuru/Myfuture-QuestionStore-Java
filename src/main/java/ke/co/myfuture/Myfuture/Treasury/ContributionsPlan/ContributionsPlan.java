package ke.co.myfuture.Myfuture.Treasury.ContributionsPlan;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
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
    Double individualContributorTarget = 0.0;
    Integer pinPriority = 1;
    @ManyToOne
    PeopleGroup peopleGroup;
    @OneToOne
    Account expenseAccount;
//    @OneToMany
    @Transient
    List<Account> moneyInAccounts;

    @Embedded
    AuditTrails auditTrails = new AuditTrails();
}
