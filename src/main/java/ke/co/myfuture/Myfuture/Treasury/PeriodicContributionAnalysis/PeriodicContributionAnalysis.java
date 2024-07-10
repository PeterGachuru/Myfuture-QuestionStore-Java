package ke.co.myfuture.Myfuture.Treasury.PeriodicContributionAnalysis;


import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class PeriodicContributionAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    Date countDate;

    @Column(nullable = false)
    Double amount;

    @ManyToOne
    @JoinColumn(nullable = false)
    Account account;

    @Transient
    Long accountId;
}