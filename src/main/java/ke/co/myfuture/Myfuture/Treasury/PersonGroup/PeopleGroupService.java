package ke.co.myfuture.Myfuture.Treasury.PersonGroup;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.GroupAccess.GroupAccessService;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PeopleGroupService {
    @Autowired
    PeopleGroupRepository repository;

    @Autowired
    GroupAccessService groupAccessService;

    public UniversalResponse savePeopleGroup(PeopleGroup account) {
        if (account.getParentId() != null && account.getParentId() > 0) {
            Optional<PeopleGroup> peopleGroup = repository.findById(account.getParentId());

            peopleGroup.ifPresent(account::setParent);
        }

        PeopleGroup savedPeopleGroup = repository.save(account);
        groupAccessService.saveGroupAccess(UserRequestContext.getCurrentUserName(), savedPeopleGroup.getId());
        System.out.println(savedPeopleGroup);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedPeopleGroup);
        response.setStatusCode(201);
        return response;
    }
}
