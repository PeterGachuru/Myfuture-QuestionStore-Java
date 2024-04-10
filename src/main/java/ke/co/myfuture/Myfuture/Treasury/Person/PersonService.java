package ke.co.myfuture.Myfuture.Treasury.Person;

import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.Person.PersonRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    @Autowired
    PersonRepository repository;

    public UniversalResponse savePerson(Person account) {
        Person savedPerson = repository.save(account);
        System.out.println(savedPerson);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedPerson);
        response.setStatusCode(201);
        return response;
    }
}
