package ke.co.myfuture.Myfuture.CurriQuestionApprove;

import ke.co.myfuture.Myfuture.CurriQuestion.CurriQuestion;
import ke.co.myfuture.Myfuture.Users.Users;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"job", "question"})})
public class CurriQuestionApprove {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    Long job;
    @ManyToOne
    @JoinColumn(name = "question")
    CurriQuestion question;
    @CreationTimestamp
    Date createdAt;
    @Column(nullable = false)
    Boolean reviewed;
    Date dateReviewed;
}