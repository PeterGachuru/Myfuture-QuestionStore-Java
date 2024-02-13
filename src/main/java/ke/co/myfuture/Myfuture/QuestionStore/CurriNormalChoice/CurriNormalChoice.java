package ke.co.myfuture.Myfuture.QuestionStore.CurriNormalChoice;

import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestion;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class CurriNormalChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "question")
    CurriQuestion question;
    String value;
    Boolean accepted;
    String type;

    Boolean hasImage;
    String imageCode;
}
