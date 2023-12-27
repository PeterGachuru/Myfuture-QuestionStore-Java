package ke.co.myfuture.Myfuture.Dukazote.ProductCategory;

import ke.co.myfuture.Myfuture.Dukazote.Cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

//    @Query()
    List<ProductCategory> findAllByAuditTrails_DeletedFlag(boolean b);
}
