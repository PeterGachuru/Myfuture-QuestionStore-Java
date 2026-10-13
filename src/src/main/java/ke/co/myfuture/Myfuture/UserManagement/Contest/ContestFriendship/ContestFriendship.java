package ke.co.myfuture.Myfuture.UserManagement.Contest.ContestFriendship;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
@Table(
        name = "contest_friendship",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "friend_id"})
        },
        indexes = {
                @Index(name = "idx_contest_friendship_student", columnList = "student_id"),
                @Index(name = "idx_contest_friendship_friend", columnList = "friend_id"),
                @Index(name = "idx_contest_friendship_rating", columnList = "rating")
        }
)
public class ContestFriendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /*
     * The pair is always stored in canonical order:
     * the smaller student ID is student, the larger is friend.
     * This prevents duplicate A->B / B->A rows.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private IbukaStudentAccount student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "friend_id", nullable = false)
    private IbukaStudentAccount friend;

    /*
     * Number of contests in which both students were invited/participants.
     */
    @Column(nullable = false)
    private Integer rating = 0;
}
