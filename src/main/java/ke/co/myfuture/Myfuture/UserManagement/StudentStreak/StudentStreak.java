package ke.co.myfuture.Myfuture.UserManagement.StudentStreak;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_learning_streak", uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_learning_streak_student", columnNames = "student_id")
})
@Getter
@Setter
@NoArgsConstructor
public class StudentStreak {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private IbukaStudentAccount student;

    @Column(nullable = false)
    private Integer currentStreak = 0;

    @Column(nullable = false)
    private Integer longestStreak = 0;

    private LocalDate lastActivityDate;

    @Column(nullable = false)
    private Boolean streakActiveToday = false;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
