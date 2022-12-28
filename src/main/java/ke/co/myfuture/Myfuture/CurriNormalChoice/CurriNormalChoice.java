package ke.co.myfuture.Myfuture.CurriNormalChoice;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class CurriNormalChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    Long question;
    String value;
    Boolean accepted;
    String type;

}
