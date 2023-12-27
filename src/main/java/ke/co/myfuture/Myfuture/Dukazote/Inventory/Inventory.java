package ke.co.myfuture.Myfuture.Dukazote.Inventory;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Dukazote.CartItem.CartItem;
import ke.co.myfuture.Myfuture.Dukazote.InventoryItem.InventoryItem;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @OneToMany
    List<InventoryItem> cartItems;

    @Embedded
    AuditTrails auditTrails = new AuditTrails();
}
