package ke.co.myfuture.Myfuture.Treasury.PersonGroup;


import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import lombok.Data;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class PeopleGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    Boolean active = true;
    String name;
//    String label;
    String notes;
    @ManyToOne
    private PeopleGroup parent;

    @Transient
    private Long parentId;
//    @OneToMany

    @Transient
    List<Person> members;

//    @OneToMany

    @Transient
    List<ContributionsPlan> plans;
    @Transient
    List<PeopleGroup> children;

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
