package ke.co.myfuture.Myfuture.Tuabudu.Singlist;

import ke.co.myfuture.Myfuture.Tuabudu.Song.Song;
import lombok.Data;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Data
public class Singlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    public String name;

    @OneToMany
    List<Song> songs;
}
