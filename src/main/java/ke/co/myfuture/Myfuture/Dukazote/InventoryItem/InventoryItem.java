package ke.co.myfuture.Myfuture.Dukazote.InventoryItem;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Dukazote.Product.Product;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false, length = 12)
    public String productCode;

    @Column(nullable = false)
    public String productName;

    @Column( nullable = false)
    Integer count;

    @Column( nullable = false)
    Double costPerItem;

    @Column( nullable = false)
    Double totalCost;

    @Transient
    Product product;

    @Embedded
    AuditTrails auditTrails = new AuditTrails();
}
