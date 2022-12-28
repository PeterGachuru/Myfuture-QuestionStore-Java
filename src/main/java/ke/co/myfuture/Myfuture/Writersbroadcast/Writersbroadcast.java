package ke.co.myfuture.Myfuture.Writersbroadcast;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Writersbroadcast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Lob
    String html;
    @CreationTimestamp
    Date date;
}
