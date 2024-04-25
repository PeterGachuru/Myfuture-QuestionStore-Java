package ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TranEntryRepository extends JpaRepository<TranEntry, Long> {
    @Query(nativeQuery = true, name = "SELECT * FROM tran_entry WHERE tran_id = :tranId")
    List<TranEntry> findByTransactionId(@Param("tranId") Long tranId);
}
