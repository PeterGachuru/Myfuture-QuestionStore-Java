package ke.co.myfuture.Myfuture.Treasury.Demands.DemandOverpaymentUsage;

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
public class DemandOverpaymentUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private DemandPayment sourcePayment; // payment that caused the overpayment

    @ManyToOne(optional = false)
    private Demand sourceDemand; // original demand that was overpaid

    @ManyToOne(optional = false)
    private Demand targetDemand; // demand where overpayment was applied

    private Double amountUsed;

    private Date usedOn;
}
