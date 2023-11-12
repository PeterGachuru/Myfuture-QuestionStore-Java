package ke.co.myfuture.Myfuture.QuestionStore.Writersbroadcast;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WritersbroadcastRepository extends JpaRepository<Writersbroadcast, Long> {

    @Query(value = "SELECT * FROM writersbroadcast WHERE targets = 'pupils' AND date_finished_sending IS NULL", nativeQuery = true)
    List<Writersbroadcast> findToSendForPupils();
    @Query(value = "SELECT * FROM writersbroadcast WHERE targets = 'writers' AND date_finished_sending IS NULL", nativeQuery = true)
    List<Writersbroadcast> findToSendForWriters();
}

