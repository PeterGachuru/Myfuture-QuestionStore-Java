package ke.co.myfuture.Myfuture.UserManagement.Post;

import ke.co.myfuture.Myfuture.Commonauth.Install.Install;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
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

    @Column(name = "install_id", nullable = false)
    Long installId;
    @Transient
    Install install;
    @Column(nullable = false)
    Long inid;

    @OneToOne
    @JoinColumn(name = "sender_id", nullable = false)
    IbukaStudentAccount studentaccount;

    @Column(nullable = false)
    Long questionid;

    @CreationTimestamp
    public Date createdAt;
    @UpdateTimestamp
    public Date updatedAt;
}