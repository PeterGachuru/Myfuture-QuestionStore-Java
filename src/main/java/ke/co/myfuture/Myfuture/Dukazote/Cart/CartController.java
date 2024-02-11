package ke.co.myfuture.Myfuture.Dukazote.Cart;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("cart")
public class CartController {
    @Autowired
    CartRepository repository;

    @PostMapping("add")
    public ResponseEntity<?> newCart(@RequestBody Cart cart) {
        Cart savedCart = repository.save(cart);
        System.out.println(savedCart);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedCart);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateCart(@RequestBody Cart cart) {
        Cart updatedCart = repository.save(cart);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedCart);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchCart(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Cart retrieved Successfully");
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
