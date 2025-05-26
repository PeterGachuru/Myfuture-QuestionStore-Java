package ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct;

import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"productCode", "people_group_id"}))
public class LoanProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, max = 255)
    private String name;

    @NotNull
    private String productCode;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal interestRate;

    @NotNull
    @Enumerated(EnumType.STRING)
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
    private BigDecimal maxLoanAmount;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal minLoanAmount;

    private String loanPurpose;

    private Integer gracePeriodDays;

    @Enumerated(EnumType.STRING)
    private LoanProductStatus status;

    private String description;

    @ManyToOne(optional = false)
    private PeopleGroup peopleGroup;

    public void setName(String name) {
        this.name = name;
        this.productCode = generateProductCode(name);
    }

    private String generateProductCode(String name) {
        if (name != null && name.length() > 1) {
            return name.substring(0, 2).toUpperCase();
        }
        return "NP";
    }
}


