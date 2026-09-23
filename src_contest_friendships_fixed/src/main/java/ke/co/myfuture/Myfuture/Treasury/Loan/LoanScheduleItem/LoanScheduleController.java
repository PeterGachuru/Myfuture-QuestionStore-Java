package ke.co.myfuture.Myfuture.Treasury.Loan.LoanScheduleItem;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/treasury/loan-schedule")
@RequiredArgsConstructor
public class LoanScheduleController {

    private final LoanScheduleItemRepository loanScheduleItemRepository;

    @GetMapping("/by-loan")
    public ResponseEntity<List<LoanScheduleItem>> getByLoanId(@RequestParam Long loanId) {
        List<LoanScheduleItem> items = loanScheduleItemRepository.findByLoan_Id(loanId);
        return ResponseEntity.ok(items);
    }
}
