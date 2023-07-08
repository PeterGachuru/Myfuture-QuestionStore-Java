package ke.co.myfuture.Myfuture.Curriculum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

    @Query(value = "SELECT * FROM curriculum ORDER BY id", nativeQuery = true)
    List<Curriculum> getAllCurriculums();
}
