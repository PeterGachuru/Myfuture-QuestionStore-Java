package ke.co.myfuture.Myfuture.Treasury.GroupAccess;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class GroupAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    PeopleGroup peopleGroup;

    @ManyToOne
    @JoinColumn(nullable = false)
    Person person;

    @Column(nullable = false)
    String username;

    @Column(nullable = false)
    Long loginUserId;

    @Transient
    Long groupId;
    @Transient
    Long personId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    GroupAccessRole role;

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
