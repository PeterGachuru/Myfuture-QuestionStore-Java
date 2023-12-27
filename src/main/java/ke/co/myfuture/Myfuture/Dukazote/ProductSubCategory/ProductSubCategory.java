package ke.co.myfuture.Myfuture.Dukazote.ProductSubCategory;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class ProductSubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false, unique = true, length = 8)
    public String code;

    @Column(nullable = false, length = 20)
    String name;

    @Embedded
    AuditTrails auditTrails = new AuditTrails();
}
