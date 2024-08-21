package ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TranEntryRepository extends JpaRepository<TranEntry, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM tran_entry WHERE tran_id = :tranId")
    List<TranEntry> findByTransactionId(@Param("tranId") Long tranId);
    @Query(nativeQuery = true, value = """
SELECT SUM(IF(tran_type='CREDIT', amount, amount*-1)) summed FROM tran_entry WHERE account_id = :accountId AND 
tran_id IN(SELECT id FROM (SELECT * FROM transaction WHERE id IN(SELECT tran_id FROM tran_entry WHERE account_id = :accountId)) AS k WHERE created_at > :fromDate)
""")
    Double netCreditsForAccountAfterDate(@Param("accountId") Long accountId, @Param("fromDate") String fromDate);
}
