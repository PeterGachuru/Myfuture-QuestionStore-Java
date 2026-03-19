package ke.co.myfuture.Myfuture.UserManagement.Referral;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferralRepository extends JpaRepository<Referral, Long> {
    List<Referral> findAllByOrderByIdDesc(Pageable pageable);

    List<Referral> findByReferrerStudentId(Long id);
}
