package ke.co.myfuture.Myfuture.UserManagement.Post;

import ke.co.myfuture.Myfuture.UserManagement.Install.Install;
import ke.co.myfuture.Myfuture.UserManagement.Studentaccount.StudentAccount;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"inid", "install_id"})})
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @OneToOne
    @JoinColumn(name = "install_id", nullable = false)
    Install install;
    @Column(nullable = false)
    Long inid;

    @OneToOne
    @JoinColumn(name = "sender_id", nullable = false)
    StudentAccount studentaccount;

    @Column(nullable = false)
    Long questionid;

    @CreationTimestamp
    public Date createdAt;
    @UpdateTimestamp
    public Date updatedAt;
}