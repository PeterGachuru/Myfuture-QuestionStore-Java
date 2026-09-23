package ke.co.myfuture.Myfuture.Treasury.Demands.DemandPayment;

import ke.co.myfuture.Myfuture.Treasury.Demands.Demand;
import ke.co.myfuture.Myfuture.Treasury.Transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Demand demand;

    @ManyToOne(optional = false)
    private Transaction transaction;

    private Double amountPaid;

    private Date paymentDate;

    private Boolean isOverpayment = false;

    private String overpaymentRoutedTo; // e.g., "Savings Account", or another demand ID
}
