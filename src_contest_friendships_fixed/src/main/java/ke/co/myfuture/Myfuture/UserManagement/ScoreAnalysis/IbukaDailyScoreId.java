package ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;


@Data
public class IbukaDailyScoreId implements Serializable {

    private LocalDate date;

    private Long studentId;

}