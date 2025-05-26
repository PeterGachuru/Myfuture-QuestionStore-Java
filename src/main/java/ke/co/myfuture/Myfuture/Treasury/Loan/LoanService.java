package ke.co.myfuture.Myfuture.Treasury.Loan;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountRepository;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlanRepository;
import ke.co.myfuture.Myfuture.Treasury.Loan.LoanApprover.LoanApprovalRepository;
import ke.co.myfuture.Myfuture.Treasury.Loan.LoanApprover.LoanApproveDTO;
import ke.co.myfuture.Myfuture.Treasury.Loan.LoanApprover.LoanApproval;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.Person.PersonRepository;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroupRepository;
import ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct.LoanProduct;
import ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct.LoanProductRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final PersonRepository personRepository;
    private final LoanProductRepository loanProductRepository;
    private final AccountRepository accountRepository;
    private final PeopleGroupRepository peopleGroupRepository;
    private final ContributionsPlanRepository contributionsPlanRepository;
    private final LoanApprovalRepository loanApprovalRepository;
    public UniversalResponse createLoan(CreateLoanRequest request) {
        System.out.println(request);
        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new RuntimeException("Person not found"));

        LoanProduct loanProduct = loanProductRepository.findById(request.getLoanProductId())
                .orElseThrow(() -> new RuntimeException("Loan Product not found"));

        Account savingAccount = null;
        if (request.getDisburseThroughSavings()) {
            savingAccount = accountRepository.findById(request.getDisbursementAccountId())
                    .orElseThrow(() -> new RuntimeException("Saving Account not found"));
        }

        // Validate loan amount
        if (request.getRequestedAmount().compareTo(loanProduct.getMinLoanAmount()) < 0 ||
                request.getRequestedAmount().compareTo(loanProduct.getMaxLoanAmount()) > 0) {
            throw new RuntimeException("Loan amount must be between " + loanProduct.getMinLoanAmount() + " and " + loanProduct.getMaxLoanAmount());
        }

        // Validate duration
        if (request.getRequestedDurationMonths() < loanProduct.getMinDurationMonths() ||
                request.getRequestedDurationMonths() > loanProduct.getMaxDurationMonths()) {
            throw new RuntimeException("Duration must be between " + loanProduct.getMinDurationMonths() + " and " + loanProduct.getMaxDurationMonths() + " months");
        }

        // Create and save loan
        Loan loan = new Loan();
        loan.setBorrower(person);
        loan.setDisburseThroughSavings(request.getDisburseThroughSavings());
        loan.setDisbursementAccount(savingAccount);
        loan.setLoanProduct(loanProduct);
        loan.setName(request.getName());
        loan.setBackdate(request.getBackdate());
        loan.setReasonForBackdating(request.getReasonForBackdating());
        loan.setBackdatedDisbursementDate(request.getBackdatedDisbursementDate());
        loan.setRequestedAmount(request.getRequestedAmount());
        loan.setRequestedDurationMonths(request.getRequestedDurationMonths());
        loan.setStatus(LoanStatus.PENDING);
        loan.setLoanPurpose(request.getLoanPurpose());
        loan.setApplicationDate(new Date());
        Optional<PeopleGroup> peopleGroup = peopleGroupRepository.findById(request.groupId);
        Optional<ContributionsPlan> contributionsPlan = contributionsPlanRepository.findById(request.planId);
        System.out.println(peopleGroup);
        System.out.println(contributionsPlan);
        if (peopleGroup.isEmpty() || contributionsPlan.isEmpty())
            return null;
        if(!loan.createAccount(contributionsPlan.get(),
                peopleGroup.get()))
            return null;
        System.out.println(loan.getAccount());
        Account loanAccount = accountRepository.save(loan.getAccount());
        loan.setAccount(loanAccount);

        loanRepository.save(loan);

        return new UniversalResponse(201,  loan, "Loan created successfully");
    }
    public UniversalResponse updateLoan(CreateLoanRequest request) {
        System.out.println(request);
        if (request.getId() == null) {
            System.out.println("Request has no id");
            return null;
        }
        Optional<Loan> existingLoanOptional = loanRepository.findById(request.getId());
        if (existingLoanOptional.isEmpty()) return null;
        Loan loan = existingLoanOptional.get();

        if (!loan.getBorrower().getId().equals(request.getPersonId())) {
            System.out.println( request.getPersonId()+" vs "+loan.getBorrower().getId());
            System.out.println("Not same borrower");
            return null;
        }
        if (!loan.getLoanProduct().getId().equals(request.getLoanProductId())) {
            System.out.println("Not same loan product");
            return null;
        }
        if (loan.getAccount().getBalance() != 0) {
            System.out.println("Account balance not zero");
            return null;
        }

        LoanProduct loanProduct = loanProductRepository.findById(request.getLoanProductId())
                .orElseThrow(() -> new RuntimeException("Loan Product not found"));

        Account savingAccount = null;
        if (request.getDisburseThroughSavings()) {
            savingAccount = accountRepository.findById(request.getDisbursementAccountId())
                    .orElseThrow(() -> new RuntimeException("Saving Account not found"));
        }

        // Validate loan amount
        if (request.getRequestedAmount().compareTo(loanProduct.getMinLoanAmount()) < 0 ||
                request.getRequestedAmount().compareTo(loanProduct.getMaxLoanAmount()) > 0) {
            throw new RuntimeException("Loan amount must be between " + loanProduct.getMinLoanAmount() + " and " + loanProduct.getMaxLoanAmount());
        }

        // Validate duration
        if (request.getRequestedDurationMonths() < loanProduct.getMinDurationMonths() ||
                request.getRequestedDurationMonths() > loanProduct.getMaxDurationMonths()) {
            throw new RuntimeException("Duration must be between " + loanProduct.getMinDurationMonths() + " and " + loanProduct.getMaxDurationMonths() + " months");
        }

        loan.setDisburseThroughSavings(request.getDisburseThroughSavings());
        loan.setDisbursementAccount(savingAccount);
        loan.setBackdate(request.getBackdate());
        loan.setName(request.getName());
        loan.setReasonForBackdating(request.getReasonForBackdating());
        loan.setBackdatedDisbursementDate(request.getBackdatedDisbursementDate());
        loan.setRequestedAmount(request.getRequestedAmount());
        loan.setRequestedDurationMonths(request.getRequestedDurationMonths());
        loan.setStatus(LoanStatus.PENDING);
        loan.setLoanPurpose(request.getLoanPurpose());
        System.out.println(loan.getAccount());
        Account loanAccount = accountRepository.save(loan.getAccount());
        loan.setAccount(loanAccount);

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


    public Optional<Loan> findById(Long id) {
        return loanRepository.findById(id);
    }

    public UniversalResponse approveLoan(LoanApproveDTO request) {
        LoanApproval loanApproval = new LoanApproval();
        loanApproval.setApprovalStatus(request.getApprovalStatus());
        Loan loan = loanRepository.findById(request.getLoanId()).get();
        loanApproval.setLoan(loan);
        loanApprovalRepository.disableAllForApproverAndLoan(loan.getId(), UserRequestContext.getCurrentUserName());
        LoanApproval savedApproval = loanApprovalRepository.save(loanApproval);

        int numberOfApprovals = loanApprovalRepository.findApprovalsCount(loan.getId());

        Boolean hasReject = loanApprovalRepository.hasReject(loan.getId());

        if (loan.getLoanProduct().getNumberOfApproversRequired() >= numberOfApprovals && !hasReject){
            loan.setStatus(LoanStatus.APPROVED);
            loanRepository.save(loan);
        }

        return new UniversalResponse(201,  savedApproval, "Approved successfully");
    }
}
