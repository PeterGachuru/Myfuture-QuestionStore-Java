package ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct;

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
public class LoanProductService {

    private final LoanProductRepository loanProductRepository;
    private final PeopleGroupRepository peopleGroupRepository;

    @Transactional
    public LoanProduct createLoanProduct(LoanProductRequestDTO dto) {
        // Validate group exists
        PeopleGroup group = peopleGroupRepository.findById(dto.getPeopleGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

        // Duration check
        if (dto.getMinDurationMonths() > dto.getMaxDurationMonths()) {
            throw new IllegalArgumentException("Min duration cannot be more than max duration");
        }

        // Amount check
        if (dto.getMinLoanAmount().compareTo(dto.getMaxLoanAmount()) > 0) {
            throw new IllegalArgumentException("Min loan amount cannot be more than max amount");
        }

        // Auto-generate product code if missing
        String code = dto.getProductCode();
        if (code == null || code.isBlank()) {
            code = generateCodeFromName(dto.getName());
        }

        // Check uniqueness within the group
        boolean exists = loanProductRepository.existsByProductCodeAndPeopleGroup(code, group);
        if (exists) {
            throw new IllegalArgumentException("Product code already exists in the group");
        }

        // Save the product
        LoanProduct product = new LoanProduct();
        product.setName(dto.getName());
        product.setProductCode(code);
        product.setInterestRate(dto.getInterestRate());
        product.setInterestRateType(dto.getInterestRateType());
        product.setMinDurationMonths(dto.getMinDurationMonths());
        product.setMaxDurationMonths(dto.getMaxDurationMonths());
        product.setMinLoanAmount(dto.getMinLoanAmount());
        product.setMaxLoanAmount(dto.getMaxLoanAmount());
        product.setLoanPurpose(dto.getLoanPurpose());
        product.setGracePeriodDays(dto.getGracePeriodDays());
        product.setDescription(dto.getDescription());
        product.setStatus(LoanProductStatus.ACTIVE); // default to active
        product.setPeopleGroup(group);

        return loanProductRepository.save(product);
    }

    private String generateCodeFromName(String name) {
        return name != null && name.length() >= 2
                ? name.substring(0, 2).toUpperCase()
                : "NP";
    }


    public UniversalResponse getLoanProductsByGroup(Long peopleGroupId) {
        List<LoanProduct> products = loanProductRepository.findByPeopleGroupId(peopleGroupId);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Loan products retrieved successfully");
        response.setEntity(products);
        response.setStatusCode(200);

        return response;
    }

    public Optional<LoanProduct> findById(Long id) {
        return loanProductRepository.findById(id);
    }
}
