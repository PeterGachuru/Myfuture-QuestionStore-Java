package ke.co.myfuture.Myfuture.Dukazote.InventoryItem;

import ke.co.myfuture.Myfuture.Dukazote.Cart.Cart;
import ke.co.myfuture.Myfuture.Dukazote.ProductCategory.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    List<InventoryItem> findAllByAuditTrails_DeletedFlag(boolean b);
}
