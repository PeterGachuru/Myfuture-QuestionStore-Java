package ke.co.myfuture.Myfuture.Treasury.PaymentMethod;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    @Query(nativeQuery = true, value = "select updated_at AS updatedAt, created_by AS createdBy, created_at AS createdAt, deleted_flag AS deletedFlag from payment_method where id = :id")
    AuditTrails.Retriever getAudits(@Param("id") Long id);

    @Query(nativeQuery = true, value = "select * from payment_method where deleted_flag = :deletedFlag AND DATE(created_at) >= DATE(:startDate) AND DATE(created_at) <= :endDate")
    List<PaymentMethod> findAllByAuditTrails_DeletedFlagOrderByAuditTrails_CreatedAtDesc(@Param("deletedFlag") boolean deletedFlag, @Param("startDate") String startDate, @Param("endDate") String endDate);

}
