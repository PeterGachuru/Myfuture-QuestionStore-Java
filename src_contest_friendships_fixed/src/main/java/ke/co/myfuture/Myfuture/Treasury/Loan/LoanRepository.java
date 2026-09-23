package ke.co.myfuture.Myfuture.Treasury.Loan;

import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("""
    SELECT l FROM Loan l
    WHERE (:groupId IS NULL OR l.borrowerGroup.id = :groupId)
      AND (:planId IS NULL OR l.account.contributionsPlan.id = :planId)
    ORDER BY l.createdAt DESC
""")
    List<Loan> findRecentLoans(@Param("groupId") Long groupId,
                               @Param("planId") Long planId,
                               Pageable pageable);
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.loanProduct.id = :loanProductId AND l.status IN :statuses")
    long countByLoanProductIdAndStatuses(
            @Param("loanProductId") Long loanProductId,
            @Param("statuses") List<LoanStatus> statuses
    );

    List<Loan> findByNextDueDateBeforeAndFullyRepaidFalse(Date date);

    Optional<Loan> findByAccount(Account account);

}


