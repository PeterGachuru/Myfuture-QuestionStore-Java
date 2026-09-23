package ke.co.myfuture.Myfuture.Commonauth.Auth.Otp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    @Query(nativeQuery = true, value = "delete from otp where email =:email")
    void deleteAllByEmail(String email);
    List<Otp> findAllByEmail(String email);

    List<Otp> findTopByEmailOrderByIdDesc(String email);
    List<Otp> findByJwt(String jwt);

    @Query(nativeQuery = true, value = "update otp set retries = 0 where email =:email")
    void updateRetries(String email);
}
