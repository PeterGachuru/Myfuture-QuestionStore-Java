package ke.co.myfuture.Myfuture.Dukazote.Product;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Dukazote.Cart.Cart;
import ke.co.myfuture.Myfuture.Dukazote.ProductCategory.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByAuditTrails_DeletedFlag(boolean b);

    @Query(nativeQuery = true, value = "select updated_at AS updatedAt, created_by AS createdBy, created_at AS createdAt, deleted_flag AS deletedFlag from product where id = :id")
    AuditTrails.Retriever getAudits(@Param("id") Long id);
}
