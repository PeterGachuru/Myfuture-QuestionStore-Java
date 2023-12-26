package ke.co.myfuture.Myfuture.Commonauth.Auth.UserRole;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Role.RoleConfig;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByUserAndRole(@NonNull User u, @NonNull RoleConfig r);
    Optional<UserRole> findByUser_Email(String email);
    List<UserRole> findAllByUser(@NonNull User u);

    @Query(nativeQuery = true, value = "Update user_role_config set role =:role_id where user=:user")
    void updateUserRole(Long role_id, Long user);

    List<UserRole> findAllByUserAndStatus(@NonNull User user,  Integer s);
}
