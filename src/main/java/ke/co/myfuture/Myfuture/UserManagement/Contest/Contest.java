package ke.co.myfuture.Myfuture.UserManagement.Contest;

import ke.co.myfuture.Myfuture.UserManagement.Studentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInvitee;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contestquestion.ContestQuestion;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    public Integer invitesCount;

    @Column(nullable = false)
    public Long classlevelId;

    @Column(nullable = false)
    public Long subjectId;

    @Column(unique = true)
    public Long cgroup ;

    @Column(nullable = false)
    String creatorName;

    @OneToOne(targetEntity = IbukaStudentAccount.class)
    @JoinColumn(name = "creator_id", referencedColumnName = "id", nullable = false)
    public IbukaStudentAccount creator;

    @OneToMany
    @JoinColumn(name = "contest")
    public List<ContestInvitee> contestInvitees;

    @OneToMany
    @JoinColumn(name = "contest")
    public List<ContestQuestion> contestQuestions;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    public Date createdAt;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    public Date updatedAt = new Date();
}