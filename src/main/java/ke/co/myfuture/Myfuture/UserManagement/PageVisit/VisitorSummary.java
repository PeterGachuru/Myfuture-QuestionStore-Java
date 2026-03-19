package ke.co.myfuture.Myfuture.UserManagement.PageVisit;

import java.time.LocalDateTime;

public interface VisitorSummary {
    String getVisitorId();
    Long getVisitCount();
    LocalDateTime getLastVisitTime();
}