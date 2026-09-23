package ke.co.myfuture.Myfuture.UserManagement.ClicksAnalyticsEvent;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsService {

    private final AnalyticsRepository repository;

    public AnalyticsService(AnalyticsRepository repository) {
        this.repository = repository;
    }

    public void save(List<AnalyticsEventRequest> requests) {

        List<AnalyticsEvent> events = new ArrayList<>();

        for (AnalyticsEventRequest request : requests) {

            AnalyticsEvent event = new AnalyticsEvent();

            event.setInid(request.getInid());
            event.setEventName(request.getEventName());
            event.setScreenName(request.getScreenName());
            event.setDescription(request.getDescription());
            event.setAppVersion(request.getAppVersion());
            event.setInstallId(request.getInstallId());
            event.setEventTime(request.getEventTime());

            events.add(event);
        }

        repository.saveAll(events);
    }
}