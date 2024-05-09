package ke.co.myfuture.Myfuture.Treasury.PersonGroup;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PeopleGroupRepository extends JpaRepository<PeopleGroup, Long> {
    @Query(nativeQuery = true, value = "select updated_at AS updatedAt, created_by AS createdBy, created_at AS createdAt, deleted_flag AS deletedFlag from people_group where id = :id")
    AuditTrails.Retriever getAudits(@Param("id") Long id);

    @Query(nativeQuery = true, value = "select * from people_group where deleted_flag = :deletedFlag AND DATE(created_at) >= DATE(:startDate) AND DATE(created_at) <= :endDate")
    List<PeopleGroup> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(nativeQuery = true, value = "select * from people_group where deleted_flag = :deletedFlag")
    List<PeopleGroup> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag);

    @Query(nativeQuery = true, value = "select * from people_group where deleted_flag = :deletedFlag AND parent_id = :parentId")

    List<PeopleGroup> findAllByAuditTrails_DeletedFlag(@Param("deletedFlag") boolean deletedFlag, @Param("parentId") Long parentId);
}