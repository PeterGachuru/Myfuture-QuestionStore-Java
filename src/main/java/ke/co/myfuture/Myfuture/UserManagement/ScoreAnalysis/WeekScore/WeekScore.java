package ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.WeekScore;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class WeekScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer position;
    @Column(nullable = false)
    Integer score;
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    Long studentId;
    @Column(nullable = false)
    Long classLevelId;
    @Transient
    String classLevel;
    String school;
    @CreationTimestamp
    Date createdAt;
}