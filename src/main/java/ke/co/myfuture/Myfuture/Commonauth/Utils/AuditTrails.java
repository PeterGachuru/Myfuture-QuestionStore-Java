package ke.co.myfuture.Myfuture.Commonauth.Utils;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.UserRequestContext;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.util.Date;

@Embeddable
@Data
public class AuditTrails {

//    @UpdateTimestamp
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
