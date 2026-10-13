package ke.co.myfuture.Myfuture.Treasury.GroupAccess;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupAccessRepository extends JpaRepository<GroupAccess, Long> {
    @Query(nativeQuery = true, value = "select updated_at AS updatedAt, created_by AS createdBy, created_at AS createdAt, deleted_flag AS deletedFlag from group_access where id = :id")
    AuditTrails.Retriever getAudits(@Param("id") Long id);

    @Query(nativeQuery = true, value = "select * from group_access where deleted_flag = :deletedFlag AND DATE(created_at) >= DATE(:startDate) AND DATE(created_at) <= :endDate")
    List<GroupAccess> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(nativeQuery = true, value = "select * from group_access where deleted_flag = :deletedFlag")
    List<GroupAccess> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag);

    @Query(nativeQuery = true, value = "select username from group_access where people_group_id = :groupId")
    List<String> findAllUsernames(@Param("groupId") Long groupId);

    @Query(nativeQuery = true, value = "select * from group_access where people_group_id = :groupId")
    List<GroupAccess> findForGroup(@Param("groupId") Long groupId);

    @Query(nativeQuery = true, value = "select * from group_access where people_group_id = :groupId AND person_id IN (SELECT id FROM person WHERE email = :emailAddress)")

    Optional<GroupAccess> findByUserIdAndGroupId(@Param("emailAddress") String emailAddress, @Param("groupId") Long groupId);
}
