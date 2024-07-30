package ke.co.myfuture.Myfuture.Treasury.FBDemo;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class FBDemo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    String username;
    String password;
}
