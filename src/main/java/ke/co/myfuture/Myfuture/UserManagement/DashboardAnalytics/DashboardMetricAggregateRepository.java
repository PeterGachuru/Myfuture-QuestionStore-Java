package ke.co.myfuture.Myfuture.UserManagement.DashboardAnalytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface DashboardMetricAggregateRepository extends JpaRepository<DashboardMetricAggregate, Long> {
    List<DashboardMetricAggregate> findByMetricKeyAndResolutionAndBucketStartGreaterThanEqualAndBucketStartLessThanOrderByBucketStart(
            String metricKey, String resolution, Date from, Date to);

    void deleteByMetricKeyAndResolutionAndBucketStartGreaterThanEqualAndBucketStartLessThan(
            String metricKey, String resolution, Date from, Date to);

    @Modifying
    @Query(value = "INSERT INTO dashboard_metric_aggregate (metric_key, resolution, bucket_start, metric_value, updated_at) " +
            "VALUES (:metricKey, :resolution, :bucketStart, :metricValue, :updatedAt) " +
            "ON DUPLICATE KEY UPDATE metric_value = VALUES(metric_value), updated_at = VALUES(updated_at)",
            nativeQuery = true)
    void upsertBucket(@Param("metricKey") String metricKey,
                      @Param("resolution") String resolution,
                      @Param("bucketStart") Date bucketStart,
                      @Param("metricValue") BigDecimal metricValue,
                      @Param("updatedAt") Date updatedAt);
}
