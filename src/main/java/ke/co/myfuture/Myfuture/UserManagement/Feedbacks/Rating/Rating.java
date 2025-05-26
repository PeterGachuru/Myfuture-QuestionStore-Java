package ke.co.myfuture.Myfuture.UserManagement.Feedbacks.Rating;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    private int rating;

    @Column(nullable = false)
    private String source;

    @Column(length = 5000)
    private String message;

    @ManyToOne
    IbukaStudentAccount ibukaStudentAccount;

    //    @UpdateTimestamp
    Date updatedAt;

//    @CreationTimestamp

    @Column(updatable = false)
    Date createdAt;

    Date deletedAt;

    @Transient
    private Long studentId;

//    @Column(nullable = false)
    String createdBy;
    String updatedBy;
    String deletedBy;

    public void delete() {
        this.deletedAt = new Date();
        this.deletedBy = UserRequestContext.getCurrentUserName();
    }

    @PrePersist
    public void prePersist() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
        this.createdBy = UserRequestContext.getCurrentUserName();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();

        this.updatedBy = UserRequestContext.getCurrentUserName();
    }
}