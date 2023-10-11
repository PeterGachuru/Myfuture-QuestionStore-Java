package ke.co.myfuture.Myfuture.QuestionStore.PassageQuestion;

import ke.co.myfuture.Myfuture.QuestionStore.Passage.Passage;
import ke.co.myfuture.Myfuture.QuestionStore.PassageChoice.PassageChoice;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class PassageQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    @ManyToOne
    @JoinColumn(name = "passage")
    Passage passage;

    String value;

    @OneToMany(mappedBy = "question")
    List<PassageChoice> choices;
}