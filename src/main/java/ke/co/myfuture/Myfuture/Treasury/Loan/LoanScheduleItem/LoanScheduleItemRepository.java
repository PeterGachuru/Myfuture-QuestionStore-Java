package ke.co.myfuture.Myfuture.Treasury.Loan.LoanScheduleItem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanScheduleItemRepository extends JpaRepository<LoanScheduleItem, Long> {
    List<LoanScheduleItem> findByLoan_Id(Long loanId);
}