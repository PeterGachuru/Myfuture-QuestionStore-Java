package ke.co.myfuture.Myfuture.UserManagement.contest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContestRepository extends JpaRepository<Contest, Long> {
    @Query(value = "SELECT * FROM contest WHERE id > :contestId", nativeQuery = true)
    List<Contest> contestsAfter(@Param("contestId") Long contestId);
}
