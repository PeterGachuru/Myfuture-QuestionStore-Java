package ke.co.myfuture.Myfuture.QuestionStore.Writersbroadcast;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class BroadcastSentTo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    public String email;

    @CreationTimestamp
    public Date createdAt;


    @ManyToOne()
    Writersbroadcast writersbroadcast;
}
