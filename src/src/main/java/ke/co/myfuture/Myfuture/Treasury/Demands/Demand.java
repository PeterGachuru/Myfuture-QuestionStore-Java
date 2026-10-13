package ke.co.myfuture.Myfuture.Treasury.Demands;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Demands.DemandBreakdown.DemandBreakdown;
import ke.co.myfuture.Myfuture.Treasury.Demands.DemandPayment.DemandPayment;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Demand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reference; // optional: e.g. "LOAN2025-0001-SCH-3"


    @Enumerated(EnumType.STRING)
    private DemandType demandType;

    @ManyToOne(optional = false)
    private Person obligatedMember;

    @ManyToOne(optional = false)
    private Account accountToCredit;


    @ManyToOne
    private Account accountToDebit;

    private Double totalAmount;

    private Date dueDate;

    private Boolean settled = false;

    private Boolean preGenerated = false;

    @OneToMany(mappedBy = "demand", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<DemandBreakdown> breakdowns = new ArrayList<>();

    @OneToMany(mappedBy = "demand", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<DemandPayment> payments = new ArrayList<>();

    private Double overpaidAmount = 0.0; // if overpaid, store here

    private String remarks;


    Date updatedAt;
    String updatedBy;

//    @CreationTimestamp

    @Column(updatable = false, nullable = false)
    Date createdAt;

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
}