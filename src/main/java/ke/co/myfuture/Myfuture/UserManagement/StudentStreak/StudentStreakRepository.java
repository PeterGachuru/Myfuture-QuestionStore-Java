package ke.co.myfuture.Myfuture.UserManagement.StudentStreak;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentStreakRepository extends JpaRepository<StudentStreak, Long> {
    Optional<StudentStreak> findByStudent_Id(Long studentId);
    List<StudentStreak> findTop500ByOrderByUpdatedAtDesc();
}
