package ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry;

import ke.co.myfuture.Myfuture.Treasury.Transaction.StatementItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    @Query("SELECT new ke.co.myfuture.Myfuture.Treasury.Transaction.StatementItemDTO(te.amount, te.tranType, te.particulars, te.balanceAfter, t.tranDate) " +
            "FROM TranEntry te JOIN te.transaction t " +
            "WHERE te.accountId = :accountId " +
            "ORDER BY t.tranDate DESC")
    Page<StatementItemDTO> findByAccountIdOrderByTranDateDesc(@Param("accountId") Long accountId, Pageable pageable);
}
