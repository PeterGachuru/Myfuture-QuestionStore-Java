package ke.co.myfuture.Myfuture.Treasury.Products.SavingsProduct;

import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingsProductRepository extends JpaRepository<SavingsProduct, Long> {

    boolean existsByProductCodeAndPeopleGroup(String productCode, PeopleGroup peopleGroup);

    List<SavingsProduct> findByPeopleGroupId(Long peopleGroupId);
}
