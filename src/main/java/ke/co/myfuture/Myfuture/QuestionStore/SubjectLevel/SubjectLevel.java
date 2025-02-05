package ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class SubjectLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "subject")
    Subject subject;

    @ManyToOne
    @JoinColumn(name = "curri_level")
    CurriLevel curriLevel;
//    @CreationTimestamp
    public Date createdAt;

    Date updatedAt;

    Date deletedAt;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deletedFlag = false;

    @Column(nullable = false)
    String createdBy;

    String deletedBy;

    public void delete() {
        this.deletedAt = new Date();
        this.deletedFlag = true;
        this.deletedBy =  UserRequestContext.getCurrentUserName();
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
}