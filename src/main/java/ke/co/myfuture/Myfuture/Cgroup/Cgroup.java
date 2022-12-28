package ke.co.myfuture.Myfuture.Cgroup;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Cgroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    String description;
    @Column(nullable = false)
    String type;

    @CreationTimestamp
    Date date;
    @UpdateTimestamp
    Date updatedAt;

}
