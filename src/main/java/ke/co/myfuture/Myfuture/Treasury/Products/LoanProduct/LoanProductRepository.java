package ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct;

import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
    boolean existsByProductCodeAndPeopleGroup(String productCode, PeopleGroup peopleGroup);

    List<LoanProduct> findByPeopleGroupId(Long peopleGroupId);
}
