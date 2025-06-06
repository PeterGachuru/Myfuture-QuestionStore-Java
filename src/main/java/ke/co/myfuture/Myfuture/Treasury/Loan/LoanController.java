package ke.co.myfuture.Myfuture.Treasury.Loan;

import ke.co.myfuture.Myfuture.Treasury.Loan.LoanScheduleItem.LoanScheduleItem;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/treasury/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final LoanScheduleGenerator loanScheduleGenerator;

    @PostMapping
    public ResponseEntity<UniversalResponse> createLoan(@RequestBody @Valid CreateLoanRequest request) {
        UniversalResponse response = loanService.createLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<UniversalResponse> updateLoan(@RequestBody @Valid CreateLoanRequest request) {
        UniversalResponse response = loanService.updateLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("approve")
    public ResponseEntity<UniversalResponse> approve(@RequestBody @Valid LoanActionsDTO request) {
        UniversalResponse response = loanService.approveLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("disburse")
    public ResponseEntity<UniversalResponse> disburse(@RequestBody @Valid LoanActionsDTO request) {
        UniversalResponse response = loanService.disburseLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/recent-loans")
    public ResponseEntity<List<LoanSummaryDTO>> getRecentLoans(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<LoanSummaryDTO> loans = loanService.getRecentLoans(limit);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetch(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Person retrieved Successfully");
        response.setEntity(loanService.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/schedule")
    public ResponseEntity<List<LoanScheduleItem>> getLoanSchedule(@RequestBody @Valid CreateLoanRequest request) {
        List<LoanScheduleItem> schedule = loanScheduleGenerator.generateSchedule(request);
        return ResponseEntity.ok(schedule);
    }
}
