package ke.co.myfuture.Myfuture.UserManagement.DeletionRequest;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Data
public class DeletionRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    String phoneNumber;
    String emailAddress;
    String detailsToDelete;
    String reasonForDeletion;
    @CreationTimestamp
    Date createdAt;
    @UpdateTimestamp
    Date updatedAt;

    Date dateDeleted;
}
