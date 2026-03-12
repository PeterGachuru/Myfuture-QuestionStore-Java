package ke.co.myfuture.Myfuture.UserManagement.PageStudyStat;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
public class PageStudyStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long topicId;

    private String visitorId;

    private Integer timeSpentSeconds;

    private Integer scrollPercent;

    private Boolean bounced;

    private LocalDateTime visitTime;

    // getters and setters
}
