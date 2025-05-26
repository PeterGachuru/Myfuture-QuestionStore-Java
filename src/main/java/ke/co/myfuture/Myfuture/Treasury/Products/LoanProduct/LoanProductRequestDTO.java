package ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class LoanProductRequestDTO {
    @Min(1)
    Long id;

    @NotNull
    @Size(min = 3)
    private String name;

    private String productCode; // Optional – will be generated if null

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal interestRate;

    @NotNull
    private InterestRateType interestRateType;

    @NotNull
    @Min(1)
    private Integer minDurationMonths;

    @NotNull
    @Min(1)
    private Integer maxDurationMonths;
    @NotNull
    @Min(1)
    private Integer numberOfApproversRequired;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal minLoanAmount;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal maxLoanAmount;

    private String loanPurpose;

    private Integer gracePeriodDays;

    private String description;

    @NotNull
    private Long peopleGroupId; // The group ID
}
