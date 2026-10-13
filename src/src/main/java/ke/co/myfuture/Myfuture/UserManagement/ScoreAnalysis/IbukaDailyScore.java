package ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Entity
@Table(name = "ibuka_daily_score")
@IdClass(IbukaDailyScoreId.class)
@Data
public class IbukaDailyScore {


    @Id
    @Column(name = "date")
    private LocalDate date;



    @Id
    @Column(name = "student_id")
    private Long studentId;



    @Column(name = "total_score")
    private Integer totalScore;

}