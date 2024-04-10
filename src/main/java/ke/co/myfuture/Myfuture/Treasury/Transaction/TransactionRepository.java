package ke.co.myfuture.Myfuture.Treasury.Transaction;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(nativeQuery = true, value = "select updated_at AS updatedAt, created_by AS createdBy, created_at AS createdAt, deleted_flag AS deletedFlag from transaction where id = :id")
    AuditTrails.Retriever getAudits(@Param("id") Long id);


    @Query(nativeQuery = true, value = "select * from transaction where deleted_flag = :deletedFlag AND DATE(created_at) >= DATE(:startDate) AND DATE(created_at) <= :endDate")
    List<Transaction> findAllByAuditTrails_DeletedFlagOrderByAuditTrails_CreatedAtDesc(@Param("deletedFlag") boolean deletedFlag, @Param("startDate") String startDate, @Param("endDate") String endDate);

}
