package ke.co.myfuture.Myfuture.Treasury.Loan;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CreateLoanRequest {

    @NotNull
    private Long personId;

    @NotNull
    private Long loanProductId;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;

    @NotNull
    @Min(1)
    private Integer durationMonths;

    private String purpose;
}
