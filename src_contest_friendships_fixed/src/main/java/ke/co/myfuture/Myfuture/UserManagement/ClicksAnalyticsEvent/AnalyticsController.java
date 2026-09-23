package ke.co.myfuture.Myfuture.UserManagement.ClicksAnalyticsEvent;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clicks/analytics")
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(
            @RequestBody List<AnalyticsEventRequest> events
    ) {

        service.save(events);

        return ResponseEntity.ok().build();
    }
}