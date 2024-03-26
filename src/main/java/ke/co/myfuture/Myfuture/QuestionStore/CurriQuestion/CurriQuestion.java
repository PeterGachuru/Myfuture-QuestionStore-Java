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

    String explanation;

    @Column(nullable = false)
    String bookModel;

    Long job;
    Long curriJob;
    @ManyToOne
    @JoinColumn(name = "subtopic")
    CurriTopic subtopic;
    @Column(nullable = false)
    Boolean sharable = false;
    @Column(nullable = false)
    Boolean reviewed = false;

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
    Integer imageLevel = 1;

    Boolean hasImage;
    Boolean choicesWithImages;
    String imageCode;

    Integer imageHeight;
    Integer imageWidth;

    @OneToMany(mappedBy =  "question")
//    @Transient
    List<CurriNormalChoice> choices;

//    public void updateChoices()
//    {
//        for (CurriNormalChoice curriNormalChoice: choices) {
//            curriNormalChoice.setQuestion(this);
//        }
//    }
}
