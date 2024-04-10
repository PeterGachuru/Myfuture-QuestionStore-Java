package ke.co.myfuture.Myfuture.Treasury.PersonGroup;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PeopleGroupService {
    @Autowired
    PeopleGroupRepository repository;

    public UniversalResponse savePeopleGroup(PeopleGroup account) {
        PeopleGroup savedPeopleGroup = repository.save(account);
        System.out.println(savedPeopleGroup);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedPeopleGroup);
        response.setStatusCode(201);
        return response;
    }
}
