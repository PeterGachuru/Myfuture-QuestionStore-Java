package ke.co.myfuture.Myfuture.Tuabudu.Singer;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Singer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    public String name;
    public String countryCode;
    public String alias;
}
