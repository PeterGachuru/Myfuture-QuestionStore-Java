package ke.co.myfuture.Myfuture.QuestionStore.CurriNotes;

import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CurriNotesRepository extends JpaRepository<CurriNotes, Long> {
    Page<CurriNotes> findBySubtopicIdOrderByCreatedAtDescIdDesc(Long subtopicId, Pageable pageable);
    Optional<CurriNotes> findBySubtopic(CurriTopic subtopic);

    Optional<CurriNotes> findBySubtopicIdAndDeletedFlagFalse(Long subtopicId);
}
