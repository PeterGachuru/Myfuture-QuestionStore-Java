package ke.co.myfuture.Myfuture.Dukazote.CartItem;

import ke.co.myfuture.Myfuture.Dukazote.Cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
