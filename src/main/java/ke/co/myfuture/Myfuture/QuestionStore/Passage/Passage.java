package ke.co.myfuture.Myfuture.QuestionStore.Passage;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Passage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    Long level;
    String subject;
    @Lob
    String string;
    String approved;
    String deleted;
    Long writer;
    Long approver;
    Long datewritten;
    Long dateapproved;
    String sharable;
}