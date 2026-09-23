package ke.co.myfuture.Myfuture.UserManagement.Feedbacks.Rating;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findTop300ByOrderByCreatedAtDesc();
    List<Rating> findByDeletedAtIsNullOrderByIdDesc(Pageable pageable);

    List<Rating>  findByIbukaStudentAccount(IbukaStudentAccount student);
}
