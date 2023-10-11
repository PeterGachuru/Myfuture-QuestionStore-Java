package ke.co.myfuture.Myfuture.QuestionStore.Writersbroadcast;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Writersbroadcast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    String subject;
    @Lob
    String html;
    @CreationTimestamp
    Date createdAt;
    @UpdateTimestamp
    Date updatedAt;
    String targets;

    Date dateSent;
    Date dateFinishedSending;

    Integer countSentTo;
    Integer targetCount;
}
