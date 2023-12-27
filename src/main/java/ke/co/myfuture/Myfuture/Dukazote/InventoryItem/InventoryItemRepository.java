package ke.co.myfuture.Myfuture.Dukazote.InventoryItem;

import ke.co.myfuture.Myfuture.Dukazote.Cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
}
