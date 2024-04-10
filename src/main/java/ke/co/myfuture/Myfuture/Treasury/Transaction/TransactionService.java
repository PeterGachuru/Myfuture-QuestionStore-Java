package ke.co.myfuture.Myfuture.Treasury.Transaction;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository repository;

    public UniversalResponse saveTransaction(Transaction account) {
        Transaction savedTransaction = repository.save(account);
        System.out.println(savedTransaction);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedTransaction);
        response.setStatusCode(201);
        return response;
    }
}
