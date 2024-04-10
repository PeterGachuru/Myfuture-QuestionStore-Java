package ke.co.myfuture.Myfuture.Treasury.PaymentMethod;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String notes;

    @Embedded
    AuditTrails auditTrails = new AuditTrails();
}
