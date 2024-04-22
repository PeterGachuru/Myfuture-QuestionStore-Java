package ke.co.myfuture.Myfuture.Treasury.Account;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query(nativeQuery = true, value = "select updated_at AS updatedAt, created_by AS createdBy, created_at AS createdAt, deleted_flag AS deletedFlag from account where id = :id")
    AuditTrails.Retriever getAudits(@Param("id") Long id);

    @Query(nativeQuery = true, value = "select * from account where deleted_flag = :deletedFlag AND DATE(created_at) >= DATE(:startDate) AND DATE(created_at) <= :endDate")
    List<Account> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(nativeQuery = true, value = "select * from account where deleted_flag = :deletedFlag")
    List<Account> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag);

    @Query(nativeQuery = true, value = "select * from account where deleted_flag = :deletedFlag AND contributions_plan_id = :planId")

    List<Account> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag, @Param("planId") Long planId);

    @Query(nativeQuery = true, value = "select * from account where owner_id = :id AND ownership_type = :ownershipType")
    Optional<Account> findAccountForPersonByType(@Param("id") Long id, @Param("ownershipType") AccountOwnershipType ownershipType);
}