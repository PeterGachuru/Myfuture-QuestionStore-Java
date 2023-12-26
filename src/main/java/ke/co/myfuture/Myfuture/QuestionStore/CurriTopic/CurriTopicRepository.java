package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurriTopicRepository extends JpaRepository<CurriTopic, Long> {

    @Query(value = "SELECT * FROM curri_topic  WHERE parent is null AND subject = :subject AND curri_level = :classLevel AND deleted = 0 ORDER BY numbering ASC", nativeQuery = true)
    List<CurriTopic> findBySubjectAndClass(@Param("subject") Long subject, @Param("classLevel")  Long classLevel);
    @Query(value = "SELECT * FROM curri_topic  WHERE parent = :parentId ORDER BY numbering ASC", nativeQuery = true)
    List<CurriTopic> findByParent(@Param("parentId") Long parentId);
}
