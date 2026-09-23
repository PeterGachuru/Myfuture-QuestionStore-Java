package ke.co.myfuture.Myfuture.UserManagement.MessageViewer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageViewerRepository extends JpaRepository<MessageViewer, Long> {
    @Query(value = "SELECT * FROM message_viewer WHERE id > :messageviewerId", nativeQuery = true)
    List<MessageViewer> messageviewersAfter(@Param("messageviewerId") Long messageviewerId);
}
