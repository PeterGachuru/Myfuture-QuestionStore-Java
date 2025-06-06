package ke.co.myfuture.Myfuture.Treasury.Loan;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query(value = "SELECT l FROM Loan l ORDER BY l.disbursementDate DESC")
    List<Loan> findRecentLoans(Pageable pageable);

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.loanProduct.id = :loanProductId AND l.status IN :statuses")
    long countByLoanProductIdAndStatuses(
            @Param("loanProductId") Long loanProductId,
            @Param("statuses") List<LoanStatus> statuses
    );
}


