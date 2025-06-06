package ke.co.myfuture.Myfuture.Treasury.Demands.DemandBreakdown;

import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Demands.Demand;
import ke.co.myfuture.Myfuture.Treasury.Demands.DemandPayment.DemandPayment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandBreakdown {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Demand demand;

    @Enumerated(EnumType.STRING)
    private DemandComponentType componentType; // e.g. "PRINCIPAL", "INTEREST", "FEE"

    @ManyToOne(optional = false)
    private Account accountToCredit;

    private Double amount;
}
