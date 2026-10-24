package ke.co.myfuture.Myfuture.UserManagement.DashboardAnalytics;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DashboardAggregationCheckpointRepository extends JpaRepository<DashboardAggregationCheckpoint, Long> {
    Optional<DashboardAggregationCheckpoint> findByMetricKeyAndResolution(String metricKey, String resolution);
}
