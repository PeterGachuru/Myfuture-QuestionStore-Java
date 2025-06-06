package ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct;

import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountOwnershipType;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountService;
import ke.co.myfuture.Myfuture.Treasury.Loan.LoanRepository;
import ke.co.myfuture.Myfuture.Treasury.Loan.LoanStatus;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroupRepository;
import ke.co.myfuture.Myfuture.Treasury.Products.ProductActionsDTO;
import ke.co.myfuture.Myfuture.Treasury.Products.ProductStatus;
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
    private final LoanRepository loanRepository;
    private final AccountService accountService;


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
        product.setNumberOfApproversRequired(dto.getNumberOfApproversRequired());
        product.setInterestRate(dto.getInterestRate());
        product.setInterestRateType(dto.getInterestRateType());
        product.setMinDurationMonths(dto.getMinDurationMonths());
        product.setMaxDurationMonths(dto.getMaxDurationMonths());
        product.setMinLoanAmount(dto.getMinLoanAmount());
        product.setMaxLoanAmount(dto.getMaxLoanAmount());
        product.setLoanPurpose(dto.getLoanPurpose());
        product.setGracePeriodDays(dto.getGracePeriodDays());
        product.setDescription(dto.getDescription());
        product.setStatus(ProductStatus.PENDING); // default to active
        product.setPeopleGroup(group);

        return loanProductRepository.save(product);
    }

    public long countActiveOrClosedLoans(Long productId) {
        List<LoanStatus> relevantStatuses = List.of(LoanStatus.ACTIVE, LoanStatus.CLOSED);
        return loanRepository.countByLoanProductIdAndStatuses(productId, relevantStatuses);
    }
    @Transactional
    public LoanProduct updateLoanProduct(LoanProductRequestDTO dto) {
        // Ensure the ID is provided
        if (dto.getId() == null) {
            throw new IllegalArgumentException("LoanProduct ID is required for update");
        }

        // Load existing loan product
        LoanProduct existingProduct = loanProductRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("LoanProduct not found"));

        if (countActiveOrClosedLoans(dto.getId()) > 0) {
            throw new IllegalArgumentException("Already in use, cannot modify this product");
        }

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

        // Determine final product code
        String code = dto.getProductCode();
        if (code == null || code.isBlank()) {
            code = generateCodeFromName(dto.getName());
        }

        // Check uniqueness of product code within group if changed
        if (!code.equals(existingProduct.getProductCode()) || !group.equals(existingProduct.getPeopleGroup())) {
            boolean exists = loanProductRepository.existsByProductCodeAndPeopleGroup(code, group);
            if (exists) {
                throw new IllegalArgumentException("Product code already exists in the group");
            }
        }

        // Update mutable fields
        existingProduct.setName(dto.getName());
        existingProduct.setProductCode(code);
        existingProduct.setNumberOfApproversRequired(dto.getNumberOfApproversRequired());
        existingProduct.setInterestRate(dto.getInterestRate());
        existingProduct.setInterestRateType(dto.getInterestRateType());
        existingProduct.setMinDurationMonths(dto.getMinDurationMonths());
        existingProduct.setMaxDurationMonths(dto.getMaxDurationMonths());
        existingProduct.setMinLoanAmount(dto.getMinLoanAmount());
        existingProduct.setMaxLoanAmount(dto.getMaxLoanAmount());
        existingProduct.setLoanPurpose(dto.getLoanPurpose());
        existingProduct.setGracePeriodDays(dto.getGracePeriodDays());
        existingProduct.setDescription(dto.getDescription());
        existingProduct.setPeopleGroup(group);

        // Save updated product
        return loanProductRepository.save(existingProduct);
    }

    private String generateCodeFromName(String name) {
        return name != null && name.length() >= 2
                ? name.substring(0, 2).toUpperCase()
                : "NP";
    }


    public UniversalResponse getLoanProductsByGroup(Long peopleGroupId, Long planId) {
        List<LoanProduct> products = loanProductRepository.findByPeopleGroupIdAndContributionsPlanId(peopleGroupId, planId);

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

    public UniversalResponse approveProduct(ProductActionsDTO request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("LoanProduct ID is required for update");
        }

        // Load existing loan product
        LoanProduct existingProduct = loanProductRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("LoanProduct not found"));

        if (existingProduct.getStatus() == ProductStatus.ACTIVE)
            throw new IllegalArgumentException("Already active");

        if (existingProduct.getStatus() != ProductStatus.PENDING)
            throw new IllegalArgumentException("Not in compatible state");

        if (request.getProductStatus() == ProductStatus.APPROVED) {
            existingProduct.approve();
        } else if (request.getProductStatus() == ProductStatus.REJECTED) {
            existingProduct.reject();
        }else {
            System.out.println(request);
            throw new IllegalArgumentException("Not supported operation");
        }

        LoanProduct savedProduct =  loanProductRepository.save(existingProduct);
        if (request.getProductStatus() == ProductStatus.APPROVED) {
            createInterestIncomeAccount(savedProduct);
        }

        return new UniversalResponse(201,  savedProduct, "Executed successfully");
    }

    private Boolean createInterestIncomeAccount(LoanProduct savedProduct) {
        Account account = new Account();
        account.setPeopleGroup(savedProduct.getPeopleGroup());
        account.setName(savedProduct.getName() + " Interest Income ");
        account.setPinPriority(1);
        account.setPlanId(savedProduct.getContributionsPlan().getId());
        account.setContributionsPlan(savedProduct.getContributionsPlan());
        account.setOwnershipType(AccountOwnershipType.INCOME);

        Account account1 = accountService.saveAutoCreatedAccount(account);
        savedProduct.setInterestIncomeAccount(account1);

        loanProductRepository.save(savedProduct);

        return true;
    }
}
