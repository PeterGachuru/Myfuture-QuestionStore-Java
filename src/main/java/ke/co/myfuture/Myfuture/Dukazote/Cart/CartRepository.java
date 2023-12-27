package ke.co.myfuture.Myfuture.Dukazote.Cart;

import org.bouncycastle.cms.jcajce.JcePasswordAuthenticatedRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}
