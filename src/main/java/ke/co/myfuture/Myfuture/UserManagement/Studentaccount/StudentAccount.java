package ke.co.myfuture.Myfuture.UserManagement.Studentaccount;

import ke.co.myfuture.Myfuture.UserManagement.Install.Install;
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
public class StudentAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    String school;
    Long classlevel;
    Long curriculum;
    Long totalScore;

//    @Column(nullable = false)
//    Long parent;

    @ManyToOne()
    @JoinColumn(name = "parent")
    UserAccount useraccount;
    String name;



    @OneToOne
    @JoinColumn(name = "install_id", nullable = false)
    Install install;
    @Column(nullable = false)
    Long inid;


    @CreationTimestamp
    public Date createdAt;
    @UpdateTimestamp
    public Date updatedAt;
}