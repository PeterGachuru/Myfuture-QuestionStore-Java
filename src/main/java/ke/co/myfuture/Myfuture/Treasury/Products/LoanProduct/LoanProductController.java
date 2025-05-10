package ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/treasury/loan-products")
@RequiredArgsConstructor
public class LoanProductController {

    private final LoanProductService loanProductService;

    @PostMapping
    public ResponseEntity<UniversalResponse> create(@Validated @RequestBody LoanProductRequestDTO dto) {
        try {
            LoanProduct saved = loanProductService.createLoanProduct(dto);

            UniversalResponse response = new UniversalResponse();
            response.setStatus("Success");
            response.setMessage("Loan product created successfully");
            response.setEntity(saved);
            response.setStatusCode(201);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            UniversalResponse errorResponse = new UniversalResponse();
            errorResponse.setStatus("Error");
            errorResponse.setMessage(e.getMessage());
            errorResponse.setEntity(null);
            errorResponse.setStatusCode(400);

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/group")
    public ResponseEntity<UniversalResponse> getLoanProductsByGroup(@RequestParam Long groupId) {
        UniversalResponse response = loanProductService.getLoanProductsByGroup(groupId);
        return ResponseEntity.ok(response);
    }

}
