package ke.co.myfuture.Myfuture.UserManagement.Contest.ContestFriendship;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContestFriendshipRepository extends JpaRepository<ContestFriendship, Long> {

    @Query("""
        SELECT f
        FROM ContestFriendship f
        WHERE (f.student = :student AND f.friend = :friend)
           OR (f.student = :friend AND f.friend = :student)
        """)
    Optional<ContestFriendship> findRelationship(
            @Param("student") IbukaStudentAccount student,
            @Param("friend") IbukaStudentAccount friend);

    /*
     * Recalculate all pair ratings for the students participating in a
     * newly-created contest in one query. Only pairs containing students
     * from the supplied list are returned.
     */
    @Query("""
        SELECT f
        FROM ContestFriendship f
        WHERE f.student.id IN :studentIds
          AND f.friend.id IN :studentIds
        """)
    List<ContestFriendship> findRelationshipsForStudents(
            @Param("studentIds") List<Long> studentIds);

    @Query(value = """
        SELECT
            ci1.invitee_id AS student_id,
            ci2.invitee_id AS friend_id,
            COUNT(DISTINCT ci1.contest) AS rating
        FROM contest_invitee ci1
        INNER JOIN contest_invitee ci2
            ON ci1.contest = ci2.contest
           AND ci1.invitee_id < ci2.invitee_id
        WHERE ci1.invitee_id IN (:studentIds)
          AND ci2.invitee_id IN (:studentIds)
        GROUP BY ci1.invitee_id, ci2.invitee_id
        """, nativeQuery = true)
    List<Object[]> calculateRatings(@Param("studentIds") List<Long> studentIds);

    /*
     * Used by searchContestInvitees. A relationship can be stored in either
     * canonical direction relative to the searching student.
     */
    /**
     * Returns invitee IDs ordered so that students with an existing learning
     * relationship to the searching student come first, with the strongest
     * relationship (highest contest count) first.
     *
     * This intentionally returns scalar IDs instead of IbukaStudentAccount
     * entities. With a native query, selecting s.* together with a LEFT JOIN
     * can be interpreted by Hibernate/Spring Data as an Object[] projection
     * in some versions, which causes a conversion error when the repository
     * method declares List<IbukaStudentAccount>.
     */
    @Query(value = """
        SELECT s.id
        FROM ibuka_student_account s
        LEFT JOIN contest_friendship f
          ON (
               (f.student_id = :studentId AND f.friend_id = s.id)
               OR
               (f.student_id = s.id AND f.friend_id = :studentId)
             )
        WHERE s.name LIKE CONCAT('%', :search, '%')
          AND s.classlevel = :classlevel
          AND s.id <> :studentId
        ORDER BY
            CASE WHEN f.id IS NULL THEN 1 ELSE 0 END ASC,
            COALESCE(f.rating, 0) DESC,
            s.id DESC
        LIMIT :count
        """, nativeQuery = true)
    List<Long> contestInviteeIdsOrderedByRelationship(
            @Param("search") String search,
            @Param("count") Integer count,
            @Param("classlevel") Long classlevel,
            @Param("studentId") Long studentId);
}
