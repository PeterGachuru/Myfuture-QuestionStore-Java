package ke.co.myfuture.Myfuture.Commonauth.Auth.Role.Privileges;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepo extends JpaRepository<Privilege, Long> {
}
