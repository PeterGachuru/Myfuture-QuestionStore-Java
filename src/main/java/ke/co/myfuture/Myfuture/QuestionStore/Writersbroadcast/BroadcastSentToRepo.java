package ke.co.myfuture.Myfuture.QuestionStore.Writersbroadcast;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface BroadcastSentToRepo extends JpaRepository<BroadcastSentTo, Long> {
//    boolean contains(String s, @Param("broadcast") Long id);

    @Query(value = "SELECT email FROM broadcast_sent_to WHERE writersbroadcast_id = :broadcast", nativeQuery = true)
    Set<String> getSent(@Param("broadcast") Long id);
}
