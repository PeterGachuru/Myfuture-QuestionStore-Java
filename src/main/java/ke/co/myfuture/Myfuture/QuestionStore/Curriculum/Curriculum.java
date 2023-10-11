package ke.co.myfuture.Myfuture.QuestionStore.Curriculum;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Curriculum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false, length = 10, unique = true)
    String name;
    @Column(nullable = false, length = 80, unique = true)
    String fullname;
    @CreationTimestamp
    Date createdAt;

    @OneToMany(mappedBy = "curriculum")
    List<CurriLevel> curriLevels;
}