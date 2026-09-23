package ke.co.myfuture.Myfuture.UserManagement.ClicksAnalyticsEvent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalyticsRepository
        extends JpaRepository<AnalyticsEvent, Long> {
    List<AnalyticsEvent> findTop3000ByInstallIdOrderByEventTimeDesc(Long installId);
}