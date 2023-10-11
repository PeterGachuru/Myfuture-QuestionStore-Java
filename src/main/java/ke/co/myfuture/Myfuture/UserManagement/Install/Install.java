package ke.co.myfuture.Myfuture.UserManagement.Install;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Install {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    String platform;

    @Column(nullable = false)
    int version;

    @CreationTimestamp
    public Date createdAt;
    public Date updatedAt = new Date();
}