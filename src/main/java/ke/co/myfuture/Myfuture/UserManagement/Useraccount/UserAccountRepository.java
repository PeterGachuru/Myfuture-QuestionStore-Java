package ke.co.myfuture.Myfuture.UserManagement.Useraccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    @Query(value = " select email from user_account where (email like '%@%.%' and email not like '% %' and email not like '@%')", nativeQuery = true)
    public List<String> getEmails();


}
