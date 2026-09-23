package ke.co.myfuture.Myfuture.QuestionStore.BrokenChoice;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
public class BrokenChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    String iscorrect;
    Long number;
    Long broken;
    String value;
}
