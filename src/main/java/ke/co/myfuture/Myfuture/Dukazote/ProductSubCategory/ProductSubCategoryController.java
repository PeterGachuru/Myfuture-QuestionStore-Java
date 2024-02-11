package ke.co.myfuture.Myfuture.Dukazote.ProductSubCategory;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("productsubcategory")
public class ProductSubCategoryController {
    @Autowired
    ProductSubCategoryRepository repository;

    @PostMapping("add")
    public ResponseEntity<?> newProductSubCategory(@RequestBody ProductSubCategory productSubCategory) {
        ProductSubCategory savedProductSubCategory = repository.save(productSubCategory);
        System.out.println(savedProductSubCategory);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedProductSubCategory);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/")
    public ResponseEntity<?> updateProductSubCategory(@RequestBody ProductSubCategory productSubCategory) {
        ProductSubCategory updatedProductSubCategory = repository.save(productSubCategory);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedProductSubCategory);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchProductSubCategory(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("ProductSubCategory retrieved Successfully");
        response.setEntity(repository.findById(id));
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
