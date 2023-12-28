package ke.co.myfuture.Myfuture.Dukazote.ProductSubCategory;

import ke.co.myfuture.Myfuture.Dukazote.Cart.Cart;
import ke.co.myfuture.Myfuture.Dukazote.ProductCategory.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
public interface ProductSubCategoryRepository extends JpaRepository<ProductSubCategory, Long> {

    List<ProductSubCategory> findAllByAuditTrails_DeletedFlag(boolean b);
}
