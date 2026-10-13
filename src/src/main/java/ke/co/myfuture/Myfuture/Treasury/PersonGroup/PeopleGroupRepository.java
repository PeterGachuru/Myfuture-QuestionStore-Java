package ke.co.myfuture.Myfuture.Treasury.PersonGroup;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PeopleGroupRepository extends JpaRepository<PeopleGroup, Long> {
    @Query(nativeQuery = true, value = "select updated_at AS updatedAt, created_by AS createdBy, created_at AS createdAt, deleted_flag AS deletedFlag from people_group where id = :id")
    AuditTrails.Retriever getAudits(@Param("id") Long id);

    @Query(nativeQuery = true, value = "select * from people_group where deleted_flag = :deletedFlag AND DATE(created_at) >= DATE(:startDate) AND DATE(created_at) <= :endDate  AND id IN(SELECT people_group_id FROM group_access WHERE username = :accessEmail)")
    List<PeopleGroup> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag, @Param("startDate") String startDate, @Param("endDate") String endDate, String accessEmail);

    @Query(nativeQuery = true, value = "select * from people_group where (parent_id = 0 OR parent_id IS NULL) AND deleted_flag = :deletedFlag AND id IN(SELECT people_group_id FROM group_access WHERE username = :accessEmail)")
    List<PeopleGroup> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag, String accessEmail);

    @Query(nativeQuery = true, value = "select * from people_group where deleted_flag = :deletedFlag AND parent_id = :parentId AND id IN(SELECT people_group_id FROM group_access WHERE username = :accessEmail)")

    List<PeopleGroup> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag, @Param("parentId") Long parentId, String accessEmail);
}