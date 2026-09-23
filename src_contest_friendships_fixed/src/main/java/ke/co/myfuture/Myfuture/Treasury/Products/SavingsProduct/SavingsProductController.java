package ke.co.myfuture.Myfuture.Treasury.Products.SavingsProduct;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin("*")
@RequestMapping("/treasury/savings-products")
@RequiredArgsConstructor
public class SavingsProductController {

    private final SavingsProductService savingsProductService;

    @PostMapping
    public ResponseEntity<UniversalResponse> create(@Validated @RequestBody SavingsProductRequestDTO dto) {
        try {
            SavingsProduct saved = savingsProductService.createSavingsProduct(dto);

            UniversalResponse response = new UniversalResponse();
            response.setStatus("Success");
            response.setMessage("Savings product created successfully");
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
        response.setEntity(savingsProductService.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/group")
    public ResponseEntity<UniversalResponse> getSavingsProductsByGroup(@RequestParam Long groupId) {
        UniversalResponse response = savingsProductService.getSavingsProductsByGroup(groupId);
        return ResponseEntity.ok(response);
    }
}
