package ke.co.myfuture.Myfuture.UserManagement.Sender;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Data
public class Sender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    SenderType type;
    @Column(nullable = false)
    Long sourceId;

    @Column(nullable = false, length = 80)
    String name;

    @CreationTimestamp
    Date createdAt;
    @UpdateTimestamp
    Date updatedAt;

    Long photo;
}