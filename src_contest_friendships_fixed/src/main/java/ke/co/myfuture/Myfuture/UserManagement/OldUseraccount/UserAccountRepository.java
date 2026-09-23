package ke.co.myfuture.Myfuture.UserManagement.OldUseraccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    @Query(value = " select email from user_account where ( length(email) > 14 and email like '%@%.%'  and email not like '%,%' and email not like '% %'  and email not like '%.%.%'  and email not like '%.' and email not like '@%')", nativeQuery = true)
//    @Query(value = " select email from user_account where  email REGEXP '^[^@]+@[^@]+\\.[^@]{2,}$'", nativeQuery = true)
    public Set<String> getEmails();


    @Query(value = "SELECT * FROM user_account WHERE email = :username OR phone = :username", nativeQuery = true)
    Optional<UserAccount> findByUsername(@Param("username") String username);
}
