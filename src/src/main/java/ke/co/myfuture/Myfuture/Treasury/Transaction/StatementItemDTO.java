package ke.co.myfuture.Myfuture.Treasury.Transaction;

import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatementItemDTO {
    private Double amount;
    private TranType tranType;
    private String particulars;
    private Double balanceAfter;
    private Date tranDate;
}
