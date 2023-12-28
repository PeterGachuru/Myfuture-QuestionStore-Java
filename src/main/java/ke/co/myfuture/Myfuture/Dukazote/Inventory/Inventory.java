package ke.co.myfuture.Myfuture.Dukazote.Inventory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ke.co.myfuture.Myfuture.Commonauth.ApplicationContextProvider;
import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Dukazote.AuditsService;
import ke.co.myfuture.Myfuture.Dukazote.InventoryItem.InventoryItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
//@EqualsAndHashCode(exclude = {"inventoryItems"})
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    public Double totalAmount;

    @Column(nullable = false)
    Integer productCount;

    @Column(nullable = false)
    Integer individualCount;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "inventory_id")
    List<InventoryItem> inventoryItems;

    @Embedded()
    AuditTrails auditTrails = new AuditTrails();

    @Transient
    AuditTrails.Retriever audits;

    public AuditTrails.Retriever getAudits() {
        AuditsService auditsService = ApplicationContextProvider.bean(AuditsService.class);
        return auditsService.getAuditsForInventory(id);
    }
}
