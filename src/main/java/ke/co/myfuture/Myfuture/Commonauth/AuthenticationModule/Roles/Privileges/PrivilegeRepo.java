package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Roles.Privileges;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepo extends JpaRepository<Privilege, Long> {
}
