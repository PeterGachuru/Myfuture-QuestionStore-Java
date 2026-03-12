package ke.co.myfuture.Myfuture.UserManagement.PageVisit;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageVisitRepository extends JpaRepository<PageVisit, Long> {
    List<PageVisit> findAllByOrderByVisitTimeDesc(Pageable pageable);
}