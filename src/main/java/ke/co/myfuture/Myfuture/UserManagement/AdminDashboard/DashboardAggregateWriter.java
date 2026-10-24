package ke.co.myfuture.Myfuture.UserManagement.AdminDashboard;

import ke.co.myfuture.Myfuture.UserManagement.DashboardAnalytics.DashboardAggregationCheckpoint;
import ke.co.myfuture.Myfuture.UserManagement.DashboardAnalytics.DashboardAggregationCheckpointRepository;
import ke.co.myfuture.Myfuture.UserManagement.DashboardAnalytics.DashboardMetricAggregateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardAggregateWriter {
    private final DashboardMetricAggregateRepository aggregateRepository;
    private final DashboardAggregationCheckpointRepository checkpointRepository;

    @Transactional(transactionManager = "studentManagementTransactionManager")
    public void replaceRange(String metricKey, String resolution, Date from, Date to, Map<Date, BigDecimal> values) {
        aggregateRepository.deleteByMetricKeyAndResolutionAndBucketStartGreaterThanEqualAndBucketStartLessThan(
                metricKey, resolution, from, to);
        Date updatedAt = new Date();
        values.forEach((bucketStart, metricValue) -> aggregateRepository.upsertBucket(
                metricKey, resolution, bucketStart, metricValue, updatedAt));
    }

    @Transactional(transactionManager = "studentManagementTransactionManager")
    public DashboardAggregationCheckpoint getOrCreateCheckpoint(String metricKey, String resolution, Date initialCursor) {
        return checkpointRepository.findByMetricKeyAndResolution(metricKey, resolution)
                .orElseGet(() -> checkpointRepository.save(
                        new DashboardAggregationCheckpoint(metricKey, resolution, initialCursor)));
    }

    @Transactional(transactionManager = "studentManagementTransactionManager")
    public void updateCheckpoint(DashboardAggregationCheckpoint checkpoint, Date nextBefore, boolean complete) {
        checkpoint.setNextBefore(nextBefore);
        checkpoint.setComplete(complete);
        checkpointRepository.save(checkpoint);
    }
}
