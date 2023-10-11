package ke.co.myfuture.Myfuture.QuestionStore.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WriterUsersRepository extends JpaRepository<Users, Long> {
    @Query(value = " select email from users where (email like '%@%.%' and email not like '% %' and email not like '@%')", nativeQuery = true)
    public List<String> getEmails();
}
