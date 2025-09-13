package ke.co.myfuture.Myfuture.Treasury.Loan.LoanScheduleItem;

import ke.co.myfuture.Myfuture.Treasury.Loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LoanScheduleItemRepository extends JpaRepository<LoanScheduleItem, Long> {
    List<LoanScheduleItem> findByLoan_Id(Long loanId);
    List<LoanScheduleItem> findByDueDateBeforeAndLoan_FullyRepaidFalse(Date date);

    List<LoanScheduleItem> findByLoanAndDueDateAfterOrderByDueDateAsc(Loan loan, Date dueDate);


    Optional<LoanScheduleItem> findFirstByLoanAndDueDateAndInstallmentNumberNotNullOrderByInstallmentNumberAsc(Loan loan, Date dueDate);

}