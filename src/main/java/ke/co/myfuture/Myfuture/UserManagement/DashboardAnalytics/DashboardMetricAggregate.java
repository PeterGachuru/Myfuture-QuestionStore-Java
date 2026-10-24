package ke.co.myfuture.Myfuture.UserManagement.DashboardAnalytics;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "dashboard_metric_aggregate", uniqueConstraints = @UniqueConstraint(
        name = "uk_dashboard_metric_bucket", columnNames = {"metric_key", "resolution", "bucket_start"}))
@Getter
@Setter
@NoArgsConstructor
public class DashboardMetricAggregate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metric_key", nullable = false, length = 64)
    private String metricKey;

    @Column(nullable = false, length = 8)
    private String resolution;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "bucket_start", nullable = false)
    private Date bucketStart;

    @Column(name = "metric_value", nullable = false, precision = 24, scale = 6)
    private BigDecimal metricValue;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt = new Date();

    public DashboardMetricAggregate(String metricKey, String resolution, Date bucketStart, BigDecimal metricValue) {
        this.metricKey = metricKey;
        this.resolution = resolution;
        this.bucketStart = bucketStart;
        this.metricValue = metricValue;
        this.updatedAt = new Date();
    }
}
