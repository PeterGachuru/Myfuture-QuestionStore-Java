package ke.co.myfuture.Myfuture.CurriTopic;

import ke.co.myfuture.Myfuture.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.Subject.Subject;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class CurriTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "parent")
    CurriTopic parent;
    @Column(nullable = false)
    String name;
    @ManyToOne
    @JoinColumn(name = "curri_level")
    CurriLevel curriLevel;
    @ManyToOne
    @JoinColumn(name = "subject")
    Subject subject;

    Boolean deleted = false;
    Boolean required = true;

    @CreationTimestamp
    Date createdAt;
    @UpdateTimestamp
    Date updatedAt;
}