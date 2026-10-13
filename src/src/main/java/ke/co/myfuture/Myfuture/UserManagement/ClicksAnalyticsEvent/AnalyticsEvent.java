package ke.co.myfuture.Myfuture.UserManagement.ClicksAnalyticsEvent;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "analytics_events")
@Data
public class AnalyticsEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long inid;

    private String eventName;

    private String screenName;

    @Column(length = 2000)
    private String description;

    private String appVersion;

    private Long installId;

    private Long eventTime;

    @CreationTimestamp
    private LocalTime uploadedTime;

    public AnalyticsEvent() {
    }
}