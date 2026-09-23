package ke.co.myfuture.Myfuture.QuestionStore.Sendid;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
public class Sendid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
}
