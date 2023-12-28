package ke.co.myfuture.Myfuture.Dukazote.Cart;

import ke.co.myfuture.Myfuture.Dukazote.ProductCategory.ProductCategory;
import org.bouncycastle.cms.jcajce.JcePasswordAuthenticatedRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findAllByAuditTrails_DeletedFlag(boolean b);
}
