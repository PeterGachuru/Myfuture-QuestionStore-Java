package ke.co.myfuture.Myfuture.Commonauth.Install;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Install {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    String platform;

    String fcmToken;

    @Column(nullable = false)
    Integer version;

    String accountEmail;
    Long accountId;

    public Date accountAddedAt;

    @CreationTimestamp
    public Date createdAt;
    public Date updatedAt = new Date();
}