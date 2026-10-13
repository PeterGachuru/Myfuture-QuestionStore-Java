package ke.co.myfuture.Myfuture.Treasury.Loan;

import lombok.Data;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CreateLoanRequest {

    private Long id;

    @NotNull
    private Long personId;

    @NotNull
    private String name;

    @NotNull
    private Long loanProductId;

    @NotNull
    private Boolean disburseThroughSavings;
    private Long disbursementAccountId;
    private Long repaymentAccountId;

    @NotNull
    @DecimalMin("0.0")
    private Double requestedAmount;

    @NotNull
    @Min(1)
    private Integer requestedDurationMonths;

    private String loanPurpose;

    @NotNull
    Long planId;

    @NotNull
    Long groupId;

    @NotNull
    private Boolean backdate;
    private String reasonForBackdating;
    private Date backdatedDisbursementDate;
}