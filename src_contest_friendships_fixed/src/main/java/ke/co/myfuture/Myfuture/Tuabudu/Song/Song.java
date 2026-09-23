package ke.co.myfuture.Myfuture.Tuabudu.Song;

import ke.co.myfuture.Myfuture.Tuabudu.MusicGenre.MusicGenre;
import ke.co.myfuture.Myfuture.Tuabudu.Language.Language;
import ke.co.myfuture.Myfuture.Tuabudu.Singer.Singer;
import lombok.Data;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Data
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @ManyToOne
    public Singer singer;
    @Column(nullable = false)
    public String title;
    @Lob
    public String content;
    public String youtubeLink;
    public String audioLink;

    @OneToMany
    List<MusicGenre> categories;

    @ManyToOne
    Language language;
}