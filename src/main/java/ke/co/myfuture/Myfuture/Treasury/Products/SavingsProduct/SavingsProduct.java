package ke.co.myfuture.Myfuture.Treasury.Products.SavingsProduct;

import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    private SavingsProductStatus status;

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
        return "SP"; // Default prefix for savings products
    }
}
