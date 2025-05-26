package ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Install.Install;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.Curriculum.Curriculum;
import ke.co.myfuture.Myfuture.UserManagement.Useraccount.UserAccount;
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

    String school;
    Long classlevel;
    Long curriculum;
    Long totalScore;
    @Transient
    Long unsyncedScore;

    @Transient
    CurriLevel curriLevel;

    @Transient
    Curriculum curriculumObject;

//    @Column(nullable = false)
//    Long parent;

    @Transient()
    UserAccount useraccount;

    @Column(nullable = false)
    Long parent;

    @Column(nullable = false)
    String parentUsername;
//    Long parent;
//    Long parent;

    String name;

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
    private User authUser;

    public void update(IbukaStudentAccount student) {
        name = student.name;
        school = student.school;
        curriculum = student.curriculum;
        classlevel = student.classlevel;
        if (student.unsyncedScore > 0) {
            totalScore += student.unsyncedScore;
        }
    }
}