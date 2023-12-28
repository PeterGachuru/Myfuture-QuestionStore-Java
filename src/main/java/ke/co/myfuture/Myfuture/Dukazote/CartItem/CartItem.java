package ke.co.myfuture.Myfuture.Dukazote.CartItem;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Dukazote.Product.Product;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column( nullable = false)
    Integer count;

    @Column( nullable = false)
    Double costPerItem;

    @Column( nullable = false)
    Double totalDiscount;

    @Column( nullable = false)
    Double totalCost;

    @Transient
    Product product;

    @Column(nullable = false, length = 12)
    public String productCode;

    @Column(nullable = false)
    public String productName;

    @Embedded
    AuditTrails auditTrails = new AuditTrails();

}
