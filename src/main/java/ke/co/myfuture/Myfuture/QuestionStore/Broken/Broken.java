package ke.co.myfuture.Myfuture.QuestionStore.Broken;

import ke.co.myfuture.Myfuture.QuestionStore.BrokenChoice.BrokenChoice;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Broken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    String subject;
    String string;
    Long level;
    String approved;
    String deleted;
    Long writer;
    Long approver;
    Long datewritten;
    Long dateapproved;
    String sharable;

    @OneToMany(mappedBy = "broken")
    List<BrokenChoice> brokenChoices;
}
