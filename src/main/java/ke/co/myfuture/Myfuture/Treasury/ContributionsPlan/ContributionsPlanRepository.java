package ke.co.myfuture.Myfuture.Treasury.ContributionsPlan;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContributionsPlanRepository extends JpaRepository<ContributionsPlan, Long> {
    @Query(nativeQuery = true, value = "select updated_at AS updatedAt, created_by AS createdBy, created_at AS createdAt, deleted_flag AS deletedFlag from contributions_plan where id = :id")
    AuditTrails.Retriever getAudits(@Param("id") Long id);

    @Query(nativeQuery = true, value = "select * from - where deleted_flag = :deletedFlag AND DATE(created_at) >= DATE(:startDate) AND DATE(created_at) <= :endDate")
    List<ContributionsPlan> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(nativeQuery = true, value = "select * from contributions_plan where deleted_flag = :deletedFlag")
    List<ContributionsPlan> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag);

    @Query(nativeQuery = true, value = "select * from contributions_plan where deleted_flag = :deletedFlag AND people_group_id = :groupId")
    List<ContributionsPlan> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag, @Param("groupId") Long groupId);

    @Query(nativeQuery = true, value = "select sum(target_amount) from account where contributions_plan_id = :planId AND deleted_flag <> 1")
    Double totalPledges(@Param("planId") Long planId);

    @Query(nativeQuery = true, value = "select sum(target_amount) from account where contributions_plan_id = :planId AND target_amount > balance  AND deleted_flag <> 1")
    Double totalPendingPledges(@Param("planId") Long planId);


    @Query(nativeQuery = true, value = "select target_amount from contributions_plan where id = :planId")
    Double totalBudget(@Param("planId") Long planId);

    @Query(nativeQuery = true, value = "select sum(balance) from account where contributions_plan_id = :planId")
    Double totalIncome(@Param("planId") Long planId);

    @Query(value = """ 
        SELECT c.id AS id, c.start_date AS startDate, c.deadline_date AS deadlineDate, 
               c.closure_date AS closureDate, c.target_type AS targetType, c.target_amount AS targetAmount, 
               c.name AS name, c.notes AS notes, c.individual_contributor_target AS individualContributorTarget, 
               c.pin_priority AS pinPriority,
               c.updated_at AS updatedAt, c.created_at AS createdAt, c.deleted_at AS deletedAt, 
               c.deleted_flag AS deletedFlag, c.created_by AS createdBy, 
               SUM(a.balance) AS availableCash 
        FROM (SELECT * FROM contributions_plan WHERE people_group_id = :groupId) c 
        LEFT JOIN (SELECT * FROM account WHERE ownership_type = 'CASH' AND contributions_plan_id IN (SELECT id FROM contributions_plan WHERE people_group_id = :groupId)) a ON c.id = a.contributions_plan_id 
        GROUP BY c.id 
    """, nativeQuery = true)
    List<ContributionsPlanInterface> findAllWithSumCash( @Param("groupId") Long groupId);
}