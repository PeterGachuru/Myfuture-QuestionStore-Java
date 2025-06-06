package ke.co.myfuture.Myfuture.Treasury.Products.SavingsProduct;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class
SavingsProductRequestDTO {

    @NotNull
    @Size(min = 3, max = 255)
    private String name;

    private String productCode;

    private String description;

    @DecimalMin("0.0")
    private BigDecimal interestRate;

    @DecimalMin("0.0")
    private BigDecimal minContributionAmount;

    @DecimalMin("0.0")
    private BigDecimal maxContributionAmount;

    @NotNull
    private Long peopleGroupId;

    @NotNull
    private Long planId;
}
