package ke.co.myfuture.Myfuture.Dukazote.CartItem;

import ke.co.myfuture.Myfuture.Dukazote.Cart.Cart;
import ke.co.myfuture.Myfuture.Dukazote.ProductCategory.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findAllByAuditTrails_DeletedFlag(boolean b);
}
