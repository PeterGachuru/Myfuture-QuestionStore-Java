package ke.co.myfuture.Myfuture.Dukazote.Product;

import ke.co.myfuture.Myfuture.Dukazote.Cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
