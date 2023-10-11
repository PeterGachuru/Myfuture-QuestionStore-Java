package ke.co.myfuture.Myfuture.UserManagement.Post.Postatempt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostattemptRepository extends JpaRepository<Postattempt, Long> {

    @Query(value = "SELECT * FROM post_attempt WHERE id > :latest_attempt AND created_at >= NOW() - INTERVAL 24 HOUR;", nativeQuery = true)
    List<Postattempt> postattemptsAfter(@Param("latest_attempt") Long latest_attempt);


}
