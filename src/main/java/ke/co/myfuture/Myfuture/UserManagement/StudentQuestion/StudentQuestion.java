package ke.co.myfuture.Myfuture.UserManagement.StudentQuestion;

import jakarta.persistence.*;
import ke.co.myfuture.Myfuture.UserManagement.Cgroup.Cgroup;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.util.Date;

@Entity
@Table(name="student_question", indexes=@Index(name="idx_student_question_created", columnList="created_at"))
@Getter @Setter
public class StudentQuestion {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="client_question_id", nullable=false, unique=true, length=80) private String clientQuestionId;
    @Column(nullable=false, length=1200) private String question;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="student_id", nullable=false) private IbukaStudentAccount studentaccount;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="group_id", nullable=false) private Cgroup cgroup;
    @Column(name="install_id", nullable=false) private Long installId;
    @Column(name="student_inid", nullable=false) private Long studentInId;
    @CreationTimestamp @Column(name="created_at", nullable=false, updatable=false) private Date createdAt;
}
