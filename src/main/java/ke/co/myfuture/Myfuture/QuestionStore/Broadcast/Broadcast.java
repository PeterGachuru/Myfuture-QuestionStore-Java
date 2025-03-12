package ke.co.myfuture.Myfuture.QuestionStore.Broadcast;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Broadcast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    String subject;
    @Lob
    String html;

    String testEmail;

    @Column(nullable = false)
    String targets;

    Date dateSent;
    Date dateFinishedSending;

    Integer countSentTo;
    Integer targetCount;

    Date updatedAt;

//    @CreationTimestamp

    @Column(updatable = false, nullable = false)
    Date createdAt;
    Date At;

    Date deletedAt;

    Boolean deletedFlag = false;

    @Column(nullable = false)
    String createdBy;
    String sentBy;

    public void delete() {
        this.deletedAt = new Date();
        this.deletedFlag = true;
    }

    public void startSend() {
        this.sentBy =  UserRequestContext.getCurrentUserName();
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
