package ke.co.myfuture.Myfuture.QuestionStore.Book;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    @Column(nullable = false)
    public String name;
    @Column(nullable = false)
    public String contentType;
    @Column(nullable = false, unique = true)
    public String model;
    @Embedded()
    AuditTrails auditTrails = new AuditTrails();
    @Transient
    AuditTrails.Retriever audits;
    public void update(Book bookFromUser) {
        name = bookFromUser.getName();
        contentType = bookFromUser.contentType;
        model = bookFromUser.model;
    }
}

/**
 * Sure, here are 20 names of valuable minerals translated into Kiswahili, ordered by their value (by mass) in ascending order:
 *
 * 1. **Madini ya Chuma** (Iron Ore)
 * 2. **Madini ya Shaba** (Copper Ore)
 * 3. **Bauxite** (Bauxite)
 * 4. **Madini ya Kijivu** (Tin Ore)
 * 5. **Madini ya Kinywe** (Zinc Ore)
 * 6. **Madini ya Kibalt** (Cobalt Ore)
 * 7. **Madini ya Nikel** (Nickel Ore)
 * 8. **Madini ya Risasi** (Lead Ore)
 * 9. **Uranium** (Uranium)
 * 10. **Madini ya Zircon** (Zircon)
 * 11. **Madini ya Platinamu** (Platinum Ore)
 * 12. **Madini ya Dhahabu** (Gold Ore)
 * 13. **Madini ya Fedha** (Silver Ore)
 * 14. **Madini ya Tantalum** (Tantalum Ore)
 * 15. **Madini ya Palladium** (Palladium Ore)
 * 16. **Madini ya Rhodium** (Rhodium Ore)
 * 17. **Madini ya Iridium** (Iridium Ore)
 * 18. **Madini ya Ruthenium** (Ruthenium Ore)
 * 19. **Madini ya Osmium** (Osmium Ore)
 * 20. **Madini ya Almasi** (Diamond)
 *
 * These minerals are listed in increasing order of value by mass, reflecting their relative economic worth on the global market.
 */
