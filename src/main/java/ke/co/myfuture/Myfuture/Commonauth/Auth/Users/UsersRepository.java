package ke.co.myfuture.Myfuture.Commonauth.Auth.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@RequestMapping
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByPhoneNo(String phone);

    List<Users> findByDeletedFlag(Character deletedFlag);

    Optional<Users> findByEmail(String email);
    List<Users> findByIsTellerAndDeletedFlag(String isTeller, Character flag);

    @Query(value = "SELECT * FROM users WHERE workclass_fk IS NULL", nativeQuery = true)
    List<Users> allWithoutWorkclass();
    @Query(value = "SELECT * FROM users WHERE sn NOT IN (SELECT user_id FROM user_roles)", nativeQuery = true)
    List<Users> allWithoutRoles();
}
