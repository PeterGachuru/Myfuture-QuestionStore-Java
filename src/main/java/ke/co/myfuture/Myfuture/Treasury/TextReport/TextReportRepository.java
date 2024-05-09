package ke.co.myfuture.Myfuture.Treasury.TextReport;

import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TextReportRepository extends JpaRepository<TextReport, Long> {
    @Query(nativeQuery = true, value = "select * from text_report where deleted_flag = :deletedFlag AND contributions_plan_id = :planId")
    List<TextReport> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag, @Param("planId") Long planId);
}
