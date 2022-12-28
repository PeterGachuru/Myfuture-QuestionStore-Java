package ke.co.myfuture.Myfuture.Sendid;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Sendid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
}
