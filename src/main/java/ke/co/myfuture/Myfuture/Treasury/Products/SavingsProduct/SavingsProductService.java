package ke.co.myfuture.Myfuture.Treasury.Products.SavingsProduct;

import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroupRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SavingsProductService {

    private final SavingsProductRepository savingsProductRepository;
    private final PeopleGroupRepository peopleGroupRepository;

    @Transactional
    public SavingsProduct createSavingsProduct(SavingsProductRequestDTO dto) {
        // Validate group exists
        PeopleGroup group = peopleGroupRepository.findById(dto.getPeopleGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

        // Auto-generate product code if missing
        String code = dto.getProductCode();
        if (code == null || code.isBlank()) {
            code = generateCodeFromName(dto.getName());
        }

        // Check uniqueness within the group
        boolean exists = savingsProductRepository.existsByProductCodeAndPeopleGroup(code, group);
        if (exists) {
            throw new IllegalArgumentException("Product code already exists in the group");
        }

        // Save the product
        SavingsProduct product = new SavingsProduct();
        product.setName(dto.getName());
        product.setProductCode(code);
        product.setDescription(dto.getDescription());
        product.setInterestRate(dto.getInterestRate());
        product.setMinContributionAmount(dto.getMinContributionAmount());
        product.setMaxContributionAmount(dto.getMaxContributionAmount());
        product.setStatus(SavingsProductStatus.ACTIVE); // default to active
        product.setPeopleGroup(group);

        return savingsProductRepository.save(product);
    }

    private String generateCodeFromName(String name) {
        return name != null && name.length() >= 2
                ? name.substring(0, 2).toUpperCase()
                : "SP";
    }

    public UniversalResponse getSavingsProductsByGroup(Long peopleGroupId) {
        List<SavingsProduct> products = savingsProductRepository.findByPeopleGroupId(peopleGroupId);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Savings products retrieved successfully");
        response.setEntity(products);
        response.setStatusCode(200);

        return response;
    }

    public Optional<SavingsProduct> findById(Long id) {
        return savingsProductRepository.findById(id);
    }
}
