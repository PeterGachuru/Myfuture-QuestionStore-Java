package ke.co.myfuture.Myfuture.Treasury.TextReport;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

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
    public void update(TextReport account) {
        this.name = account.name;
        this.template = account.template;
        this.impactType = account.impactType;
        this.notes = account.notes;
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
