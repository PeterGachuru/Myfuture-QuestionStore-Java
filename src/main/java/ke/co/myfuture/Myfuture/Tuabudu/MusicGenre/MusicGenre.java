package ke.co.myfuture.Myfuture.Tuabudu.MusicGenre;

import lombok.Data;

import javax.persistence.*;


@Entity
@Data
public class MusicGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    @Column(nullable = false)
    public String name;
}
