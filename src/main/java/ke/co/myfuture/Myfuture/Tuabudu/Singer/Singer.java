package ke.co.myfuture.Myfuture.Tuabudu.Singer;

import ke.co.myfuture.Myfuture.Tuabudu.Song.Song;
import lombok.Data;

import jakarta.persistence.*;
import java.util.List;

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

    @OneToMany
    List<Song> songList;
}
