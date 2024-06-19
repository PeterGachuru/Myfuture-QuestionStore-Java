package ke.co.myfuture.Myfuture.UserManagement.contest.ContestInvitee;

import ke.co.myfuture.Myfuture.UserManagement.Studentaccount.IbukaStudentAccount;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"contest", "invitee_id"})})
public class ContestInvitee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    public Long contest;
    @OneToOne
    @JoinColumn(name = "invitee_id", nullable = false)
    IbukaStudentAccount studentaccount;
    Integer score;
    boolean attempted;
    @CreationTimestamp
    public Date createdAt;

    public Date attemptedAt;

    boolean selfInvited;
    @UpdateTimestamp
    public Date updatedAt = new Date();
}