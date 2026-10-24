package ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/** One accepted score award, retained as an audit record and idempotency key. */
@Entity
@Table(name = "student_score_event", indexes = {
        @Index(name = "idx_student_score_event_student_received", columnList = "student_id, received_at")
})
@Getter
@Setter
public class StudentScoreEvent {
    @Id
    @Column(name = "event_id", length = 160, nullable = false, updatable = false)
    private String eventId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private IbukaStudentAccount student;

    @Column(name = "score_delta", nullable = false)
    private Long scoreDelta;

    @Column(name = "source", nullable = false, length = 120)
    private String source;

    @Column(name = "install_id")
    private Long installId;

    @Column(name = "occurred_at")
    private Instant occurredAt;

    @CreationTimestamp
    @Column(name = "received_at", nullable = false, updatable = false)
    private Instant receivedAt;
}
