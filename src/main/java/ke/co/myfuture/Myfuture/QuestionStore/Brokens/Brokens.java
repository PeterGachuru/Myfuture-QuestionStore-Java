package ke.co.myfuture.Myfuture.QuestionStore.Brokens;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Brokens {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;


    String subject;
    @Lob
    String json;
    Long level;
    String approved;
    String deleted;
    Long writer;
    Long approver;
    Long datewritten;
    Long dateapproved;
    String sharable;
}
