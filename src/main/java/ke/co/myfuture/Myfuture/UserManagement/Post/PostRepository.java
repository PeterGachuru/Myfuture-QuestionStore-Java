package ke.co.myfuture.Myfuture.UserManagement.Post;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "SELECT * FROM post WHERE id > :postId", nativeQuery = true)
    List<Post> postsAfter(@Param("postId") Long postId);

    List<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Post>  findByStudentaccount(IbukaStudentAccount student);

    @Query("""
    SELECT DATE(e.createdAt), COUNT(e)
    FROM Post e
    WHERE e.createdAt >= :startDate
    GROUP BY DATE(e.createdAt)
    ORDER BY DATE(e.createdAt)
""")
    List<Object[]> countPerDay(@Param("startDate") Date startDate);
}
