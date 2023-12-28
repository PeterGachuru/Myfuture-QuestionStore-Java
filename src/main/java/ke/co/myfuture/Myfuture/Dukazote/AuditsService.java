package ke.co.myfuture.Myfuture.Dukazote;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Dukazote.Inventory.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditsService {

    @Autowired
    InventoryRepository inventoryRepository;
    public AuditTrails.Retriever getAuditsForInventory(Long id) {
        return inventoryRepository.getAudits(id);
    }
}
