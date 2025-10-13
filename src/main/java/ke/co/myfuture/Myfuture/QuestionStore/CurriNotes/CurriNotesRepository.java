package ke.co.myfuture.Myfuture.QuestionStore.CurriNotes;

import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CurriNotesRepository extends JpaRepository<CurriNotes, Long> {
    Optional<CurriNotes> findBySubtopic(CurriTopic subtopic);
}