package ke.co.myfuture.Myfuture.UserManagement.Post.Postatempt;

import ke.co.myfuture.Myfuture.UserManagement.Post.Post;
import ke.co.myfuture.Myfuture.UserManagement.Studentaccount.StudentAccount;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"post", "student_id"})})
public class Postattempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    @OneToOne
    @JoinColumn(name = "post", nullable = false)
    Post post;
    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    StudentAccount studentaccount;
    @Column(nullable = false)
    String scored;
    @Column(nullable = false)
    Long selectedChoice;
    @CreationTimestamp
    public Date createdAt;
    @UpdateTimestamp
    public Date updatedAt = new Date();
}