package ke.co.myfuture.Myfuture.UserManagement.DashboardAnalytics;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "dashboard_aggregation_checkpoint", uniqueConstraints = @UniqueConstraint(
        name = "uk_dashboard_checkpoint_metric_resolution", columnNames = {"metric_key", "resolution"}))
@Getter
@Setter
@NoArgsConstructor
public class DashboardAggregationCheckpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metric_key", nullable = false, length = 64)
    private String metricKey;

    @Column(nullable = false, length = 8)
    private String resolution;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "next_before", nullable = false)
    private Date nextBefore;

    @Column(nullable = false)
    private boolean complete;

    public DashboardAggregationCheckpoint(String metricKey, String resolution, Date nextBefore) {
        this.metricKey = metricKey;
        this.resolution = resolution;
        this.nextBefore = nextBefore;
    }
}
