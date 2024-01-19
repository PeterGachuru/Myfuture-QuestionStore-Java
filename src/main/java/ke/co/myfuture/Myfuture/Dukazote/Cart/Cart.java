package ke.co.myfuture.Myfuture.Dukazote.Cart;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Dukazote.CartItem.CartItem;
import lombok.Data;

import javax.persistence.*;
import java.util.List;


@Entity
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    private Double totalInvoice;

    @Column(nullable = false)
    Integer productCount;

    @Column(nullable = false)
    Integer individualCount;

    @Column(nullable = false)
    private Double overallDiscount;

    @Column(nullable = false)
    private Double totalPaid;

    @Column(nullable = false)
    private Boolean paid;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_id")
    List<CartItem> cartItems;

    @Embedded
    AuditTrails auditTrails = new AuditTrails();
}
