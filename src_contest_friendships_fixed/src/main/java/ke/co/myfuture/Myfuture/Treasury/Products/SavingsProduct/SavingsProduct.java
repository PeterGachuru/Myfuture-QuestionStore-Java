package ke.co.myfuture.Myfuture.Treasury.Products.SavingsProduct;

import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.Products.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"productCode", "people_group_id"}))
public class SavingsProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, max = 255)
    private String name;

    @NotNull
    private String productCode;

    private String description;

    /**
     * Optional interest rate on savings.
     */
    @DecimalMin("0.0")
    private BigDecimal interestRate;

    /**
     * Minimum contribution amount per period (e.g. monthly).
     */
    @DecimalMin("0.0")
    private BigDecimal minContributionAmount;

    /**
     * Maximum contribution amount per period (optional).
     */
    @DecimalMin("0.0")
    private BigDecimal maxContributionAmount;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @ManyToOne(optional = false)
    private PeopleGroup peopleGroup;
    @ManyToOne(optional = false)
    private ContributionsPlan contributionsPlan;

    public void setName(String name) {
        this.name = name;
        this.productCode = generateProductCode(name);
    }

    private String generateProductCode(String name) {
        if (name != null && name.length() > 1) {
            return name.substring(0, 2).toUpperCase();
        }
        return "SP"; // Default prefix for savings products
    }
}
