package ke.co.myfuture.Myfuture.QuestionStore.QuestionImage;

import ke.co.myfuture.Myfuture.QuestionStore.Image.Image;
import ke.co.myfuture.Myfuture.QuestionStore.Users.Users;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class QuestionImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    Users requester;
    @ManyToOne
    @JoinColumn(name = "image")
    Image image;
    @CreationTimestamp
    Date createdAt;
}