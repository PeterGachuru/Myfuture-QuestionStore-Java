package ke.co.myfuture.Myfuture.SubjectLevel;

import ke.co.myfuture.Myfuture.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.Subject.Subject;
import ke.co.myfuture.Myfuture.Users.Users;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class SubjectLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "subject")
    Subject subject;

    @ManyToOne
    @JoinColumn(name = "curri_level")
    CurriLevel curriLevel;
    @CreationTimestamp
    public Date createdAt;
}