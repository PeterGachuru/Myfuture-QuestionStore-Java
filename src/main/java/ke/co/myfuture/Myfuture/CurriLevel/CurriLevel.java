package ke.co.myfuture.Myfuture.CurriLevel;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class CurriLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    Long curriculum;
    @Column(nullable = false, length = 20)
    String name;

    @Column(nullable = false)
    Integer numbering;

    @CreationTimestamp
    Date createdAt;
    @UpdateTimestamp
    Date updatedAt;
}