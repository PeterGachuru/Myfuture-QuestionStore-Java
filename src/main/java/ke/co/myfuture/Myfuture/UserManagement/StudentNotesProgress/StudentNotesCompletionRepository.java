package ke.co.myfuture.Myfuture.UserManagement.StudentNotesProgress;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentNotesCompletionRepository extends JpaRepository<StudentNotesCompletion, Long> {
    Optional<StudentNotesCompletion> findByStudent_IdAndNotesIdAndNotesUpdatedAt(
            Long studentId, Long notesId, String notesUpdatedAt
    );

    List<StudentNotesCompletion> findByStudent_IdOrderByCompletedAtDesc(Long studentId);

    List<StudentNotesCompletion> findTop1000ByOrderByCompletedAtDesc();
}
