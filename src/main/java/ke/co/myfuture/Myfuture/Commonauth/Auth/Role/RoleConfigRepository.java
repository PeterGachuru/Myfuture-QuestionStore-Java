package ke.co.myfuture.Myfuture.Commonauth.Auth.Role;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleConfigRepository extends JpaRepository<RoleConfig, Long> {
    List<RoleConfig> findByStatus(@NonNull Integer status);

    @Query(nativeQuery = true, value = "select * from role_config limit 1")
//    @Query(nativeQuery = true, value = "select * from role_config where name = :name limit 1")
    Optional<RoleConfig> findByName(@NonNull String name);

    Optional<RoleConfig> findById(@NonNull Long id);

    interface Reviewer{
       String getEmail();
    }
}
