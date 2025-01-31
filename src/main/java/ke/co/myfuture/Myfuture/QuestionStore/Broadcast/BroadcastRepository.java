package ke.co.myfuture.Myfuture.QuestionStore.Broadcast;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BroadcastRepository extends JpaRepository<Broadcast, Long> {

    @Query(value = "SELECT * FROM broadcast WHERE targets = 'pupils' AND date_finished_sending IS NULL", nativeQuery = true)
    List<Broadcast> findToSendForPupils();
    @Query(value = "SELECT * FROM broadcast WHERE targets = 'writers' AND date_finished_sending IS NULL", nativeQuery = true)
    List<Broadcast> findToSendForWriters();
}