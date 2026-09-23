package ke.co.myfuture.Myfuture.Treasury.Demands;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface DemandRepository extends JpaRepository<Demand, Long> {
    boolean existsByReference(String reference);

    List<Demand> findBySettledFalseAndDeletedFlagFalseAndDueDateBefore(Date date);
}
