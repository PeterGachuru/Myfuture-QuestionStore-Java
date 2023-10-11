package ke.co.myfuture.Myfuture.QuestionStore.Writerstatistics;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Writerstatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
}