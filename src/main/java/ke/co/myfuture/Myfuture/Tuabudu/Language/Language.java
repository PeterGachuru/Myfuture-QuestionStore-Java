package ke.co.myfuture.Myfuture.Tuabudu.Language;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    @Column(nullable = false)
    public String name;
    @Column(nullable = false)
    public Long worldSpeakersCount;
}