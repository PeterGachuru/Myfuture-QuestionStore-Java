package ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Install.Install;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.Curriculum;
import ke.co.myfuture.Myfuture.UserManagement.OldUseraccount.UserAccount;
import ke.co.myfuture.Myfuture.UserManagement.Sender.Sender;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"inid", "install_id"})})
public class IbukaStudentAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(unique = true)
    public String shareCode;

    private Long appVersion;

    String school;
    Long classlevel;
    Long curriculum;
    Long totalScore;
    @Transient
    Long unsyncedScore;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer creditsBalance;

    @OneToOne()
    @JoinColumn(name = "sender_id", unique = true)
    Sender sender;

    @Transient
    CurriLevel curriLevel;

    @Transient
    Curriculum curriculumObject;

    @Transient()
    UserAccount useraccount;

    @Column(nullable = false)
    Long parent;

    @Column(nullable = false)
    String parentUsername;

    String name;
    String firstName;
    String lastName;

    @Column(name = "install_id", nullable = false)
    Long installId;
    @Transient
    Install install;
    @Column(nullable = false)
    Long inid;

    @CreationTimestamp
    public Date createdAt;
    @UpdateTimestamp
    public Date updatedAt;
    public Date recentActivity;
    private User authUser;

    public void update(IbukaStudentAccount student) {
        name = student.name;
        firstName = student.firstName;
        lastName = student.lastName;
        school = student.school;
        curriculum = student.curriculum;
        classlevel = student.classlevel;
        if (student.unsyncedScore > 0) {
            totalScore += student.unsyncedScore;
        }
    }
}