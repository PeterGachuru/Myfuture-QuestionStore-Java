package ke.co.myfuture.Myfuture.ImageStore.FileManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
    Optional<ImageFile> findByCode(String code);

    @Query(value = "SELECT * FROM image_file WHERE description LIKE CONCAT('%', :search ,'%') OR tags LIKE description LIKE CONCAT('%', :search ,'%')", nativeQuery = true)
    List<ImageFile> search(String search);
}
