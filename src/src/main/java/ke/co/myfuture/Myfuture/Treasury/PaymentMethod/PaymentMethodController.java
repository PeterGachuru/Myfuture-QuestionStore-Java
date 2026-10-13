package ke.co.myfuture.Myfuture.Treasury.PaymentMethod;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("treasury/paymentmethod")
public class PaymentMethodController {
    @Autowired
    PaymentMethodRepository repository;

    @Autowired
    PaymentMethodService paymentMethodService;

    @PostMapping("add")
    public ResponseEntity<?> newPaymentMethod(@RequestBody PaymentMethod paymentMethod) {
        UniversalResponse response = paymentMethodService.savePaymentMethod(paymentMethod);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updatePaymentMethod(@RequestBody PaymentMethod paymentMethod) {
        PaymentMethod updatedPaymentMethod = repository.save(paymentMethod);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedPaymentMethod);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchPaymentMethod(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("PaymentMethod retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("all")
    public ResponseEntity<?> fetchProductCategory() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("ProductCategory retrieved Successfully");
        List<PaymentMethod> paymentMethodList = repository.findAll();
//        for (PaymentMethod paymentMethod: paymentMethodList)
//            paymentMethod.setAudits(repository.getAudits(paymentMethod.getId()));
        response.setEntity(paymentMethodList);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
