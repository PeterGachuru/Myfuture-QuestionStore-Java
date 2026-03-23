package ke.co.myfuture.Myfuture.UserManagement.Chatmessage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface ChatmessageRepository extends JpaRepository<Chatmessage, Long> {
    @Query(value = "SELECT * FROM chatmessage WHERE id > :chatmessageId", nativeQuery = true)
    List<Chatmessage> chatmessagesAfter(@Param("chatmessageId") Long chatmessageId);
    @Query(value = "SELECT * FROM chatmessage WHERE groupid = :groupid AND id > :chatmessageId", nativeQuery = true)
    List<Chatmessage> getMessagesForGroup(@Param("groupid") Long groupid,  @Param("chatmessageId") Long chatmessageId);

    @Query("SELECT c FROM Chatmessage c ORDER BY c.id DESC")
    List<Chatmessage> findLatestMessages(org.springframework.data.domain.Pageable pageable);

    @Query("""
    SELECT DATE(e.createdAt), COUNT(e)
    FROM Chatmessage e
    WHERE e.createdAt >= :startDate
    GROUP BY DATE(e.createdAt)
    ORDER BY DATE(e.createdAt)
""")
    List<Object[]> countPerDay(@Param("startDate") Date startDate);
}