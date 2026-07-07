package ke.co.myfuture.Myfuture.UserManagement.ClicksAnalyticsEvent;


import lombok.Data;

@Data
public class AnalyticsEventRequest {

    private Long inid;

    private String eventName;

    private String screenName;

    private String description;

    private String appVersion;

    private Long installId;

    private Long eventTime;

    public AnalyticsEventRequest() {

    }

    // Getters and setters
}