package ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion;

import ke.co.myfuture.Myfuture.QuestionStore.CurriNormalChoice.CurriNormalChoice;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
@Entity
@Data
public class CurriQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    String string;

    Long job;
    Long curriJob;
    @ManyToOne
    @JoinColumn(name = "subtopic")
    CurriTopic subtopic;
    @Column(nullable = false)
    Boolean sharable;
    @Column(nullable = false)
    Boolean reviewed;

    @CreationTimestamp
    Date datewritten;
    Date dateARequest ;
    Date dateApproved;
    @Column(unique = true)
    Long cgroup;
    Integer editionNumber = 1;
    @UpdateTimestamp
    Date updatedAt;
    @Column(nullable = false)
    Integer imageLevel;
    @OneToMany(mappedBy =  "question")
    List<CurriNormalChoice> choices;
}