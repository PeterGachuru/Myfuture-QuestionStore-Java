package ke.co.myfuture.Myfuture.Treasury.PaymentMethod;

import ke.co.myfuture.Myfuture.Treasury.PaymentMethod.PaymentMethod;
import ke.co.myfuture.Myfuture.Treasury.PaymentMethod.PaymentMethodRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentMethodService {
    @Autowired
    PaymentMethodRepository repository;

    public UniversalResponse savePaymentMethod(PaymentMethod account) {
        PaymentMethod savedPaymentMethod = repository.save(account);
        System.out.println(savedPaymentMethod);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedPaymentMethod);
        response.setStatusCode(201);
        return response;
    }
}
