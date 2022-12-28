package ke.co.myfuture.Myfuture.QuestionImage;

import ke.co.myfuture.Myfuture.Image.Image;
import ke.co.myfuture.Myfuture.Users.Users;
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