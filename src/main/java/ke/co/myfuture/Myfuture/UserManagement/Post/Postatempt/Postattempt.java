package ke.co.myfuture.Myfuture.UserManagement.Post.Postatempt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ke.co.myfuture.Myfuture.UserManagement.Post.Post;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"post", "student_id"})})
public class Postattempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "post")
    @JsonIgnore
    private Post post;
    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    IbukaStudentAccount studentaccount;
    @Column(nullable = false)
    Boolean scored;
    @Column(nullable = false)
    Long selectedChoice;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt = new Date();
}