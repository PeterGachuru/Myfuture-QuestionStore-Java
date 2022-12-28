package ke.co.myfuture.Myfuture.Useraccount;

import ke.co.myfuture.Myfuture.Install.Install;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    String phone;
    String county;
    String email;
    @Column(nullable = false)
    String password;

    @OneToOne
    @JoinColumn(name = "install_id")
    Install install;


    @CreationTimestamp
    public Date createdAt;
    @UpdateTimestamp
    public Date updatedAt = new Date();
}