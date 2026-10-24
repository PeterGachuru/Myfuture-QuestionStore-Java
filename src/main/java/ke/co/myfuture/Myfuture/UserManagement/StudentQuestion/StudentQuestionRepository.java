package ke.co.myfuture.Myfuture.UserManagement.StudentQuestion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
public interface StudentQuestionRepository extends JpaRepository<StudentQuestion, Long> {
    Optional<StudentQuestion> findByClientQuestionId(String clientQuestionId);
    @Query("SELECT q FROM StudentQuestion q WHERE q.studentaccount.classlevel IN :classLevelIds ORDER BY q.createdAt DESC")
    List<StudentQuestion> findNearby(@Param("classLevelIds") List<Long> classLevelIds, Pageable pageable);
}
