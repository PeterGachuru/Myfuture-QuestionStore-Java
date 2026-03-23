package ke.co.myfuture.Myfuture.UserManagement.PageVisit;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PageVisitRepository extends JpaRepository<PageVisit, Long> {
    List<PageVisit> findAllByOrderByVisitTimeDesc(Pageable pageable);


    @Query("""
        SELECT 
            v.visitorId AS visitorId, 
            COUNT(v) AS visitCount,
            MAX(v.visitTime) AS lastVisitTime
        FROM PageVisit v
        GROUP BY v.visitorId
        HAVING COUNT(v) > 1
        ORDER BY lastVisitTime DESC
    """)
    List<VisitorSummary> findVisitorsWithMultipleVisits();

    List<PageVisit> findByVisitorIdOrderByVisitTimeDesc(String visitorId);

    @Query(value = """
    SELECT DATE(visit_time), COUNT(*)
    FROM page_visit
    WHERE visit_time >= :startDate
    GROUP BY DATE(visit_time)
    ORDER BY DATE(visit_time)
""", nativeQuery = true)
    List<Object[]> countPerDay(@Param("startDate") LocalDateTime startDate);
}