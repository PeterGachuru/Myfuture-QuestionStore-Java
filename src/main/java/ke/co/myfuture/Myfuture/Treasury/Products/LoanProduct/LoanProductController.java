package ke.co.myfuture.Myfuture.Treasury.Products.LoanProduct;

import ke.co.myfuture.Myfuture.Treasury.Loan.LoanActionsDTO;
import ke.co.myfuture.Myfuture.Treasury.Products.ProductActionsDTO;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PutMapping
    public ResponseEntity<UniversalResponse> update(@Validated @RequestBody LoanProductRequestDTO dto) {
        try {
            LoanProduct saved = loanProductService.updateLoanProduct(dto);

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

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchPerson(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Product retrieved Successfully");
        response.setEntity(loanProductService.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/group-and-plan")
    public ResponseEntity<UniversalResponse> getLoanProductsByGroup(@RequestParam Long groupId, @RequestParam Long planId) {
        UniversalResponse response = loanProductService.getLoanProductsByGroup(groupId, planId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("approve")
    public ResponseEntity<UniversalResponse> approve(@RequestBody @Valid ProductActionsDTO request) {
        UniversalResponse response = loanProductService.approveProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
