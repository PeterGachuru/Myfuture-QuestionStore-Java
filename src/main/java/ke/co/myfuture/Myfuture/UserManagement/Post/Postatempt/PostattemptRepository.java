package ke.co.myfuture.Myfuture.UserManagement.Post.Postatempt;

import ke.co.myfuture.Myfuture.UserManagement.Post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostattemptRepository extends JpaRepository<Postattempt, Long> {

    @Query(value = "SELECT * FROM postattempt WHERE id > :latest_attempt AND created_at >= NOW() - INTERVAL 24 HOUR;", nativeQuery = true)
    List<Postattempt> postattemptsAfter(@Param("latest_attempt") Long latest_attempt);

    @Query(value = "SELECT * FROM postattempt WHERE id > :latestAttemptId AND post = :postId", nativeQuery = true)
    List<Postattempt> attemptsForPost(Long postId, Long latestAttemptId);

    @Query("""
        SELECT 
            p.post.id AS postId,
            SUM(CASE WHEN p.scored = true THEN 1 ELSE 0 END) AS correctCount,
            SUM(CASE WHEN p.scored = false THEN 1 ELSE 0 END) AS wrongCount
        FROM Postattempt p
        WHERE p.post.id IN :postIds
        GROUP BY p.post.id
    """)
    List<PostAttemptSummary> summarizeByPostIds(@Param("postIds") List<Long> postIds);


    @Query("""
        SELECT 
            p.post.id AS postId,
            SUM(CASE WHEN p.scored = true THEN 1 ELSE 0 END) AS correctCount,
            SUM(CASE WHEN p.scored = false THEN 1 ELSE 0 END) AS wrongCount
        FROM Postattempt p
        GROUP BY p.post.id
    """)
    List<PostAttemptSummary> summarizeByPostIds();

    Long countByPost(Post post);
}
