package ke.co.myfuture.Myfuture.MpesaIntegration;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MpesaTransactionRepository extends JpaRepository<MpesaTransaction, Long> {
    MpesaTransaction findByCheckoutRequestId(String checkoutRequestId);
    List<MpesaTransaction> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
