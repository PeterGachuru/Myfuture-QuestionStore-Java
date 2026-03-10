package ke.co.myfuture.Myfuture.UserManagement.SubscriptionPlan;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "subscription_plan")
@Data
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "duration_days")
    private Integer durationDays;

    private Integer price;

    private Boolean active;
}