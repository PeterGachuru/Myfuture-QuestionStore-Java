package ke.co.myfuture.Myfuture.QuestionStore.SubtopicText;

import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class SubtopicText {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    @Lob
    String content;

    @ManyToOne
    @JoinColumn(nullable = false)
    CurriTopic subTopic;
}
