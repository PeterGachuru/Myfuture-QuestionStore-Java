package ke.co.myfuture.Myfuture.Treasury.Transaction;

import ke.co.myfuture.Myfuture.Treasury.Transaction.TranEntry.TranEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/treasury/statement")
@RequiredArgsConstructor
public class AccountStatementController {

    private final TranEntryRepository tranEntryRepository;

    @GetMapping
    public ResponseEntity<Page<StatementItemDTO>> getAccountStatement(
            @RequestParam Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "40") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StatementItemDTO> statementPage = tranEntryRepository.findByAccountIdOrderByTranDateDesc(accountId, pageable);
        return ResponseEntity.ok(statementPage);
    }
}
