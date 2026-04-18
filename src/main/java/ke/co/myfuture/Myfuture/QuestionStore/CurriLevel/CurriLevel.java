package ke.co.myfuture.Myfuture.QuestionStore.CurriLevel;

import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class CurriLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false, length = 20)
    Long curriculum;
    @Column(nullable = false, length = 20)
    String name;

    @Column(length = 120, unique = true)
    private String slug;

    @Column(nullable = false)
    Integer numbering;

    Integer ageEstimate;

    @CreationTimestamp
    Date createdAt;
    @UpdateTimestamp
    Date updatedAt;

    @Transient
    List<Subject> subjects = new ArrayList<>();
}