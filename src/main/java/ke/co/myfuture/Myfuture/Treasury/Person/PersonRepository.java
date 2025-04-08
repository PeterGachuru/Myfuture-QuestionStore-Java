package ke.co.myfuture.Myfuture.Treasury.Person;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query(nativeQuery = true, value = "select updated_at AS updatedAt, created_by AS createdBy, created_at AS createdAt, deleted_flag AS deletedFlag from person where id = :id")
    AuditTrails.Retriever getAudits(@Param("id") Long id);

    @Query(nativeQuery = true, value = "select * from person where deleted_flag = :deletedFlag AND DATE(created_at) >= DATE(:startDate) AND DATE(created_at) <= :endDate")
    List<Person> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(nativeQuery = true, value = "select * from person where deleted_flag = :deletedFlag")
    List<Person> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag);

    @Query(nativeQuery = true, value = "select * from person where system_user_id = :userId AND id IN (SELECT person_id FROM member WHERE people_group_id = :groupId )")
    Optional<Person> findPersonByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);


    @Query(nativeQuery = true, value = "select * from person where id IN (SELECT person_id FROM group_access WHERE username = :loginUsername) AND id IN (SELECT person_id FROM member WHERE people_group_id = :groupId )")
    Optional<Person> findPersonByLoginUsernameAndGroupId(@Param("loginUsername") String loginUsername, @Param("groupId") Long groupId);


    @Query(nativeQuery = true, value = "select * from person where id IN (SELECT person_id FROM member WHERE people_group_id = :groupId )")
    List<Person> findPersonsByGroupId(@Param("groupId") Long id);


    @Query(nativeQuery = true, value = "select * from person where deleted_flag = :deletedFlag AND id IN (SELECT person_id FROM member WHERE people_group_id = :groupId )")

    List<Person> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag, @Param("groupId") Long groupId);

    @Query(nativeQuery = true, value = "select * from person where deleted_flag = :deletedFlag AND id IN (SELECT person_id FROM member WHERE people_group_id = :groupId AND person_id = :personId )")
    Optional<Person> foundMemberById(@Param("deletedFlag") boolean deletedFlag, @Param("groupId") Long groupId, @Param("personId") Long personId);
}