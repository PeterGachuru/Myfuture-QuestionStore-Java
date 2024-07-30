package ke.co.myfuture.Myfuture.Treasury.PeriodicContributionAnalysis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PeriodicContributionAnalysisRepository extends JpaRepository<PeriodicContributionAnalysis, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM periodic_contribution_analysis WHERE account_id = :accountId ORDER BY count_date DESC LIMIT 1")
    Optional<PeriodicContributionAnalysis> findLastForAccount(Long accountId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT DATE_FORMAT(count_date, '%Y-%m') AS ym FROM periodic_contribution_analysis WHERE account_id IN(SELECT id FROM account WHERE contributions_plan_id = :planId) ORDER BY ym;")
    List<String> allRelevantMonths(@Param("planId") Long planId);

    @Query(nativeQuery = true, value = """
            SELECT ac.name, ac.id AS accountId, pa.amount, DATE_FORMAT(count_date, '%Y-%m') AS period FROM (SELECT * FROM periodic_contribution_analysis WHERE account_id IN(SELECT id FROM account WHERE contributions_plan_id = :planId)) AS pa  
            JOIN (SELECT * FROM account WHERE contributions_plan_id = :planId) AS ac ON pa.account_id = ac.id 
            ORDER BY accountId ASC;            
            """)
    List<SummaryPeriod> getAllForPlan(@Param("planId") Long planId);

    interface SummaryPeriod {
        String getName();
        String getPeriod();
        Double getAmount();
        Long getAccountId();
    }
}
