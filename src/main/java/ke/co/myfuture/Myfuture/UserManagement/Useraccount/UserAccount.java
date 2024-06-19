package ke.co.myfuture.Myfuture.UserManagement.Useraccount;

import ke.co.myfuture.Myfuture.Commonauth.Install.Install;
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
    String firstName;
    String lastName;
    String surname;
    String status = "active";
    String email;
    @Column(nullable = false)
    String password;

    @Column(name = "install_id", nullable = false)
    Long installId;
    @Transient
    Install install;


    @CreationTimestamp
    public Date createdAt;
    public Date lastLogin;
    @UpdateTimestamp
    public Date updatedAt = new Date();

//    List<String> roles = List.of("ROLE1", "ROLE2");
}