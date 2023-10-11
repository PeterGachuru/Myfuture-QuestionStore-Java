package ke.co.myfuture.Myfuture.QuestionStore.ApproveQuestion;


import ke.co.myfuture.Myfuture.QuestionStore.Users.Users;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class ApproveQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @OneToOne
    @JoinColumn(name = "partner")
    Users user;

    @Column(nullable = false)
    Long question;

    @CreationTimestamp
    Date date;

    @Column(nullable = false)
    String accepted = "false";
}