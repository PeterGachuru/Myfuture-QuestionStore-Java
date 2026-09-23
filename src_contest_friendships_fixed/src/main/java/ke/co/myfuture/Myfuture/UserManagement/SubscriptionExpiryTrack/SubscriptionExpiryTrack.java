package ke.co.myfuture.Myfuture.UserManagement.SubscriptionExpiryTrack;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Data
public class SubscriptionExpiryTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    Long parent;

    @Column(unique = true)
    String parentUsername;

    @Column(unique = true)
    Long installId;

    public Date expiryDate;

    @CreationTimestamp
    public Date createdAt;
    @UpdateTimestamp
    public Date updatedAt;
}