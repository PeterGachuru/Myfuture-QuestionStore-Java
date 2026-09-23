package ke.co.myfuture.Myfuture.QuestionStore.ResetPass;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Data
public class ResetPass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    String email;
    String key1;
    String key2;

    Long deadline;

    String used;

    Date date;
    @CreationTimestamp
    public Date createdAt;
    @UpdateTimestamp
    public Date updatedAt = new Date();
}
