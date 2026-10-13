package ke.co.myfuture.Myfuture.UserManagement.DeletionRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeletionRequestRepo extends JpaRepository<DeletionRequest, Long> {

    List<DeletionRequest> findAllByOrderByIdDesc(Pageable pageable);
}
