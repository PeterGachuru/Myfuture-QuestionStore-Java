package ke.co.myfuture.Myfuture.ManageTopic;

import ke.co.myfuture.Myfuture.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.Users.Users;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class ManageTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    String activity;
    @ManyToOne
    @JoinColumn(name = "topic")
    CurriTopic topic;
    @ManyToOne
    @JoinColumn(name = "partner")
    Users partner;
    @CreationTimestamp
    Date createdAt;
}
