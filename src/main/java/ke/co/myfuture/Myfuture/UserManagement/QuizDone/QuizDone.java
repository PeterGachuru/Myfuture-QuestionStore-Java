package ke.co.myfuture.Myfuture.UserManagement.QuizDone;

import ke.co.myfuture.Myfuture.UserManagement.Contest.Contest;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"inid", "installId"})})
public class QuizDone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    @Column(nullable = false)
    public Long inid;
    @Column(nullable = false)
    public Long installId;

    @Column(nullable = false)
    public Integer questionsCount;

    public String category;

    @OneToOne(targetEntity = IbukaStudentAccount.class)
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    public IbukaStudentAccount student;

    @OneToOne(targetEntity = Contest.class)
    @JoinColumn(name = "contest_id", referencedColumnName = "id")
    public Contest contest;


    @Column(nullable = false)
    public Date startDate;

    public Date endDate;

    @Column(nullable = false)
    public Long subjectId;
    public  Integer appVersion;
    public  Integer score;
    public  Integer overall;
    @Column(nullable = false)
    public Boolean deleted;

    @CreationTimestamp
    @Column(nullable = false)
    public Date createdAt;
    @UpdateTimestamp
    public Date updatedAt;
}
