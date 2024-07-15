package ke.co.myfuture.Myfuture.Treasury.Account;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Treasury.Transaction.Transaction;
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

    @Query(nativeQuery = true, value = """
select * from account where deleted_flag = :deletedFlag AND contributions_plan_id = :planId 
AND ownership_type = :ownershipType 
 AND ((:allowsZeroBalance = 0 AND balance <> 0) OR :allowsZeroBalance = 1) 
 AND ((:allowsZeroPledges = 0 AND target_amount <> 0) OR :allowsZeroPledges = 1) 
 AND ((:allowsUncleared = 0 AND balance >= target_amount ) OR :allowsUncleared = 1) 
 AND ((:allowsCleared = 0 AND balance < target_amount  ) OR :allowsCleared = 1) 
""")
    List<Account>  findAllByPlanId(@Param("deletedFlag") boolean deletedFlag, @Param("planId") Long planId,  @Param("ownershipType") String ownershipType,
                                  @Param("allowsZeroBalance") Integer allowsZeroBalance, @Param("allowsZeroPledges") Integer allowsZeroPledges,
                                  @Param("allowsUncleared") Integer allowsUncleared,  @Param("allowsCleared") Integer allowsCleared);


    @Query(nativeQuery = true, value = "select * from account where deleted_flag = :deletedFlag AND people_group_id = :groupId AND ownership_type = :ownershipType")
    List<Account> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag, @Param("groupId") Long groupId,  @Param("ownershipType") String ownershipType);

    @Query(nativeQuery = true, value = "select * from account where deleted_flag = :deletedFlag  AND people_group_id = :groupId AND  contributions_plan_id = :planId AND ownership_type = :ownershipType order by id ASC")

    List<Account> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag, @Param("groupId") Long groupId, @Param("planId") Long planId, @Param("ownershipType") String ownershipType);

//    @Query(nativeQuery = true, value = "select * from account where owner_id = :id AND people_group_id = :groupId LIMIT 1")
//    Optional<Account> findAccountForPersonByType(@Param("id") Long id, @Param("groupId") Long groupId);
    @Query(nativeQuery = true, value = "select * from account where owner_id = :id AND people_group_id = :groupId AND ownership_type = :ownershipType LIMIT 1")
    Optional<Account> findAccountForPersonByType(@Param("id") Long id, @Param("groupId") Long groupId, @Param("ownershipType") String ownershipType);

    @Query(nativeQuery = true, value = "select * from account where owner_id = :personId AND contributions_plan_id = :planId")
    Optional<Account> findByPersonAndPlan(@Param("personId") Long personId, @Param("planId") Long planId);

}