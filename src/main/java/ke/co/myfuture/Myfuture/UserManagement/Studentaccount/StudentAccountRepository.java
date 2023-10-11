package ke.co.myfuture.Myfuture.UserManagement.Studentaccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentAccountRepository extends JpaRepository<StudentAccount, Long> {
    @Query(value = "SELECT * FROM(SELECT * FROM student_account WHERE name LIKE %:search% AND classlevel = :classlevel\n" +
            "AND id <> :studentId ORDER BY id DESC) AS m LIMIT :count", nativeQuery = true)
    List<StudentAccount> contestInvitees(@Param("search") String search, @Param("count")  Integer count,
                                         @Param("classlevel") Integer classlevel, @Param("studentId")  Long studentId);
}
