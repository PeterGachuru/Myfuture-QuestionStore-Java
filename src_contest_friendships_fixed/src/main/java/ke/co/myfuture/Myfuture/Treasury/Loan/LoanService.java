package ke.co.myfuture.Myfuture.Treasury.Loan;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountRepository;
import ke.co.myfuture.Myfuture.Treasury.Account.AccountService;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlan;
import ke.co.myfuture.Myfuture.Treasury.ContributionsPlan.ContributionsPlanRepository;
import ke.co.myfuture.Myfuture.Treasury.Loan.LoanApprover.LoanApprovalRepository;
import ke.co.myfuture.Myfuture.Treasury.Loan.LoanApprover.LoanApproval;
import ke.co.myfuture.Myfuture.Treasury.Loan.LoanScheduleItem.LoanScheduleItem;
import ke.co.myfuture.Myfuture.Treasury.Loan.LoanScheduleItem.LoanScheduleItemRepository;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.Person.PersonRepository;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroupRepository;
import ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct.LoanProduct;
import ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct.LoanProductRepository;
import ke.co.myfuture.Myfuture.Treasury.Transaction.SystemTransactionService;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TransactionBuilder;
import ke.co.myfuture.Myfuture.Treasury.Transaction.TransactionCategory;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SystemTransactionService systemTransactionService;
    private final AccountService accountService;
    private final LoanScheduleGenerator loanScheduleGenerator;
    private final LoanScheduleItemRepository loanScheduleItemRepository;
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


        Account repaymentAccount = accountRepository.findById(request.getRepaymentAccountId())
                .orElseThrow(() -> new RuntimeException("Repayment Account not found"));

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
        loan.setRepaymentAccount(repaymentAccount);
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


    public List<LoanSummaryDTO> getRecentLoans(int limit, Long groupId, Long planId) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Loan> recentLoans = loanRepository.findRecentLoans( groupId, planId, pageable);
        return recentLoans.stream().map(this::mapToDTO).collect(Collectors.toList());
    }


    public Optional<Loan> findById(Long id) {
        return loanRepository.findById(id);
    }

    @Transactional
    public UniversalResponse approveLoan(LoanActionsDTO request) {
        LoanApproval loanApproval = new LoanApproval();
        loanApproval.setLoanStatus(request.getLoanStatus());
        Loan loan = loanRepository.findById(request.getLoanId()).get();
        loanApproval.setLoan(loan);
        loanApprovalRepository.disableAllForApproverAndLoan(loan.getId(), UserRequestContext.getCurrentUserName());
        LoanApproval savedApproval = loanApprovalRepository.save(loanApproval);

        int numberOfApprovals = loanApprovalRepository.findApprovalsCount(loan.getId());

        Boolean hasReject = loanApprovalRepository.hasReject(loan.getId());

        if (loan.getLoanProduct().getNumberOfApproversRequired() >= numberOfApprovals && !hasReject){
            loan.setStatus(LoanStatus.APPROVED);
            loan.setApprovedAmount(loan.getRequestedAmount());
            loanRepository.save(loan);
        }

        return new UniversalResponse(201,  savedApproval, "Approved successfully");
    }

    @Transactional
    public UniversalResponse disburseLoan(LoanActionsDTO request) {
        if (request.getLoanStatus() != LoanStatus.DISBURSED) return null;

        Loan loan = loanRepository.findById(request.getLoanId()).get();

        if (loan.getStatus() != LoanStatus.APPROVED) return null;
        Account loanAccount = accountService.makeAccountActive(loan.getAccount());
        loan.setAccount(loanAccount);

        if (loan.getDisburseThroughSavings()) {
            TransactionBuilder transactionBuilder = TransactionBuilder.builder()
                    .transactionCategory(TransactionCategory.LOAN_DISBURSEMENT_BY_SAVING)
                    .creditAccount(loan.getDisbursementAccount())
                    .debitAccount(loan.getAccount())
                    .contributionsPlan(loan.getAccount().getContributionsPlan())
                    .oneOfTheAccounts(loan.getAccount())
                    .amount(loan.getApprovedAmount())
                    .creditParticulars("Loan Disbursement")
                    .debitParticulars("Loan Disbursement")
                    .build();
            UniversalResponse universalResponse = systemTransactionService.saveTransaction(transactionBuilder);

            if (universalResponse.getStatusCode() < 400) {
                loan.setDisbursed();
                Loan savedLoan = loanRepository.save(loan);
                List<LoanScheduleItem> loanScheduleItems = loanScheduleGenerator.generateSchedule(loan);
                for (LoanScheduleItem loanScheduleItem: loanScheduleItems)
                    loanScheduleItem.setLoan(savedLoan);
                loanScheduleItemRepository.saveAll(loanScheduleItems);
                System.out.println("About to modify next due date");
                if (!loanScheduleItems.isEmpty()) {
                    System.out.println("Loan schedule is not empty");
                    // Assuming the schedule is ordered by dueDate
                    Date nextDueDate = loanScheduleItems.get(0).getDueDate();
                    System.out.println("Next due date: "+nextDueDate);
                    savedLoan.setNextDueDate(nextDueDate);
                    loanRepository.save(savedLoan);
                }
                return new UniversalResponse(201,  savedLoan, "Disbursed successfully");
            }
        } else {
            TransactionBuilder transactionBuilder = TransactionBuilder.builder()
                    .transactionCategory(TransactionCategory.LOAN_DISBURSEMENT_BY_CASH)
                    .debitAccount(loan.getAccount())
                    .oneOfTheAccounts(loan.getAccount())
                    .amount(loan.getApprovedAmount())
                    .debitParticulars("Loan Disbursement")
                    .contributionsPlan(loan.getAccount().getContributionsPlan())
                    .build();
            UniversalResponse universalResponse = systemTransactionService.saveTransaction(transactionBuilder);

            if (universalResponse.getStatusCode() < 400) {
                loan.setDisbursed();
                Loan savedLoan = loanRepository.save(loan);
                List<LoanScheduleItem> loanScheduleItems = loanScheduleGenerator.generateSchedule(loan);
                for (LoanScheduleItem loanScheduleItem: loanScheduleItems)
                    loanScheduleItem.setLoan(savedLoan);
                loanScheduleItemRepository.saveAll(loanScheduleItems);
                System.out.println("About to modify next due date");
                if (!loanScheduleItems.isEmpty()) {
                    System.out.println("Loan schedule is not empty");
                    // Assuming the schedule is ordered by dueDate
                    Date nextDueDate = loanScheduleItems.get(0).getDueDate();
                    System.out.println("Next due date: "+nextDueDate);
                    savedLoan.setNextDueDate(nextDueDate);
                    loanRepository.save(savedLoan);
                }
                return new UniversalResponse(201,  savedLoan, "Disbursed successfully");
            }
        }

        return new UniversalResponse(400,  null, "Error");
    }
}