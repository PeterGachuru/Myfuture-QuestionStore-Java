package ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
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