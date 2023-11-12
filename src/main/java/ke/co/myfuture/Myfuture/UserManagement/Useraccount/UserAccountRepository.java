package ke.co.myfuture.Myfuture.UserManagement.Useraccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    @Query(value = " select email from user_account where ( length(email) > 14 and email like '%@%.%'  and email not like '%,%' and email not like '% %'  and email not like '%.%.%'  and email not like '%.' and email not like '@%')", nativeQuery = true)
//    @Query(value = " select email from user_account where  email REGEXP '^[^@]+@[^@]+\\.[^@]{2,}$'", nativeQuery = true)
    public Set<String> getEmails();


}
