package ke.co.myfuture.Myfuture.QuestionStore.Subject;

import ke.co.myfuture.Myfuture.Treasury.Account.AccountStatus;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    @Column(nullable = false, unique = true)
    String name;
    @CreationTimestamp
    public Date createdAt;
    @UpdateTimestamp
    public Date updatedAt = new Date();

    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
    SubjectCategory subjectCategory;

    @Transient
    private String firstSubtopicSlug;
}