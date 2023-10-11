package ke.co.myfuture.Myfuture.QuestionStore.Webmessages;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Webmessages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    String name;
    String email;
    String message;
    @CreationTimestamp
    Date date;
}
