package ke.co.myfuture.Myfuture.UserManagement.StudentNotesProgress;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_notes_completion", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_student_notes_version_completion",
                columnNames = {"student_id", "notes_id", "notes_updated_at"}
        )
})
@Getter
@Setter
@NoArgsConstructor
public class StudentNotesCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private IbukaStudentAccount student;

    // Notes are owned by the separate QuestionStore persistence unit, so keep
    // their identifiers as values instead of mapping a cross-database entity.
    @Column(name = "notes_id", nullable = false)
    private Long notesId;

    @Column(name = "subtopic_id", nullable = false)
    private Long subtopicId;

    /** Opaque notes version token supplied by the Android client. */
    @Column(name = "notes_updated_at", nullable = false, length = 80)
    private String notesUpdatedAt;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
