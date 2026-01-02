package ke.co.myfuture.Myfuture.UserManagement.Sender;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SenderRepository extends JpaRepository<Sender, Long> {
    @Query(value = "SELECT * FROM sender WHERE id > :senderId", nativeQuery = true)
    List<Sender> sendersAfter(@Param("senderId") Long senderId);
}