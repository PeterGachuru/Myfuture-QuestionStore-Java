package ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentScoreEventRepository extends JpaRepository<StudentScoreEvent, String> {
    List<StudentScoreEvent> findTop200ByStudent_IdOrderByReceivedAtDesc(Long studentId);
}
