package ke.co.myfuture.Myfuture.Comprehension;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Comprehension {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;


    Long level;
    String subject;
    @Lob
    String json;
    String approved;
    String deleted;
    Long writer;
    Long approver;
    Long datewritten;
    Long dateapproved;
    String sharable;
}
