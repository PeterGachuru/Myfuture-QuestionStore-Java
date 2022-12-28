package ke.co.myfuture.Myfuture.PassageChoice;

import ke.co.myfuture.Myfuture.PassageQuestion.PassageQuestion;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PassageChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    @ManyToOne
    @JoinColumn(name = "question")
    PassageQuestion question;

//    Long question;
    String value;
    String iscorrect;
}
