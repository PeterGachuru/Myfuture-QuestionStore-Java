package ke.co.myfuture.Myfuture.Dukazote.Product;

import ke.co.myfuture.Myfuture.Dukazote.Product.Product;
import ke.co.myfuture.Myfuture.Dukazote.Product.ProductRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("product")
public class ProductController {
    @Autowired
    ProductRepository repository;

    @PostMapping("add")
    public ResponseEntity<?> newProduct(@RequestBody Product product) {
        Product savedProduct = repository.save(product);
        System.out.println(savedProduct);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedProduct);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateProduct(@RequestBody Product product) {
        Product updatedProduct = repository.save(product);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedProduct);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchProduct(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Product retrieved Successfully");
        Optional<Product> product = repository.findById(id);
        if (product.isPresent())
            product.get().setAudits(repository.getAudits(id));
        response.setEntity(product.get());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<?> fetchProductCategory() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("ProductCategory retrieved Successfully");
        response.setEntity(repository.findAllByAuditTrails_DeletedFlag(false));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
