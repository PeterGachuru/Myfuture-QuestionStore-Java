package ke.co.myfuture.Myfuture.QuestionStore.Users;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Data
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner", nullable = false)
    public Long partner;
    @Column(nullable = false)
    String fullname;
    @Column(nullable = false)
    String email;
    @Column(nullable = false)
    String phone;
    @Column(nullable = false)
    @Lob
    String password;
    @Column(nullable = false)
    Long designation;
    @Column(nullable = false)
    String occupation;
    @CreationTimestamp
    public Date createdAt;
}
