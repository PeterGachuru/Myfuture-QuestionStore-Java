package ke.co.myfuture.Myfuture.Tuabudu.Song;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    public String singer;
    public String title;
    public String content;
    public String youtubeLink;
    public String audioLink;
}
