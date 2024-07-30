package ke.co.myfuture.Myfuture.Treasury.TextReport;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranType;
import lombok.Data;

import javax.persistence.*;
@Entity
@Data
public class TextReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    String name;

    @ManyToOne
    @JoinColumn(nullable = false)
    ContributionsPlan contributionsPlan;

    String notes;
    @Column(columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String template;

//    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ImpactType impactType;

    @Transient
    Long planId;

    @Embedded
    AuditTrails auditTrails = new AuditTrails();

    public void update(TextReport account) {
        this.name = account.name;
        this.template = account.template;
        this.impactType = account.impactType;
        this.notes = account.notes;
    }
}
