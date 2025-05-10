package ke.co.myfuture.Myfuture.Treasury.Loan;

import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.Person.PersonRepository;
import ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct.LoanProduct;
import ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct.LoanProductRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final PersonRepository personRepository;
    private final LoanProductRepository loanProductRepository;

    public UniversalResponse createLoan(CreateLoanRequest request) {
        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new RuntimeException("Person not found"));

        LoanProduct loanProduct = loanProductRepository.findById(request.getLoanProductId())
                .orElseThrow(() -> new RuntimeException("Loan Product not found"));

        // Validate loan amount
        if (request.getAmount().compareTo(loanProduct.getMinLoanAmount()) < 0 ||
                request.getAmount().compareTo(loanProduct.getMaxLoanAmount()) > 0) {
            throw new RuntimeException("Loan amount must be between " + loanProduct.getMinLoanAmount() + " and " + loanProduct.getMaxLoanAmount());
        }

        // Validate duration
        if (request.getDurationMonths() < loanProduct.getMinDurationMonths() ||
                request.getDurationMonths() > loanProduct.getMaxDurationMonths()) {
            throw new RuntimeException("Duration must be between " + loanProduct.getMinDurationMonths() + " and " + loanProduct.getMaxDurationMonths() + " months");
        }

        // Create and save loan
        Loan loan = new Loan();
        loan.setBorrower(person);
        loan.setLoanProduct(loanProduct);
        loan.setRequestedAmount(request.getAmount());
        loan.setRequestedDurationMonths(request.getDurationMonths());
        loan.setStatus(LoanStatus.PENDING);
        loan.setLoanPurpose(request.getPurpose());
        loan.setApplicationDate(LocalDateTime.now());

        loanRepository.save(loan);

        return new UniversalResponse(201,  loan, "Loan created successfully");
    }


    private LoanSummaryDTO mapToDTO(Loan loan) {
        return new LoanSummaryDTO(
                loan.getId(),
                loan.getBorrower().getName(),
                loan.getBorrowerGroup() != null ? loan.getBorrowerGroup().getName() : "N/A",
                loan.getRequestedAmount(),
                loan.getRequestedDurationMonths(),
                loan.getLoanPurpose(),
                loan.getApprovedAmount(),
                loan.getApprovedBy(),
                loan.getApprovalDate(),
                loan.getOutstandingBalance(),
                loan.getTotalRepaid(),
                loan.getNumberOfRepaymentsMade(),
                loan.getDisbursementDate(),
                loan.getDisbursedBy(),
                loan.getNextDueDate(),
                loan.getGracePeriodUsedDays(),
                loan.isFullyRepaid(),
                loan.getStatus(),
                loan.getApplicationDate(),
                loan.getClosedDate()
        );
    }


    public List<LoanSummaryDTO> getRecentLoans(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Loan> recentLoans = loanRepository.findRecentLoans(pageable);
        return recentLoans.stream().map(this::mapToDTO).collect(Collectors.toList());
    }


}
