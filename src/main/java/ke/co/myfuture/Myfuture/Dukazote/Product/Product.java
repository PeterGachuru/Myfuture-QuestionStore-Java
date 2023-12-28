package ke.co.myfuture.Myfuture.Dukazote.Product;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
//@ToString
//@AllArgsConstructor
//@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false, unique = true, length = 12)
    public String code;
    @Column(nullable = false, length = 8)
    public String subcategoryCode;

    @Column(nullable = false, length = 20)
    String name;
    String size;
    String make;
    String priceCurrency;
    Double sellingPrice;
    Integer countRemaining;

    @Embedded
    public AuditTrails auditTrails = new AuditTrails();

    @Transient
    AuditTrails.Retriever audits;
}