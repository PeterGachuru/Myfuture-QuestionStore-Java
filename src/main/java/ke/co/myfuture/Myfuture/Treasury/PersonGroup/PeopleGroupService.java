package ke.co.myfuture.Myfuture.Treasury.PersonGroup;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Treasury.GroupAccess.GroupAccessRole;
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

    public UniversalResponse savePeopleGroup(PeopleGroup createPeopleGroup) {
        if (createPeopleGroup.getParentId() != null && createPeopleGroup.getParentId() > 0) {
            Optional<PeopleGroup> peopleGroup = repository.findById(createPeopleGroup.getParentId());

            peopleGroup.ifPresent(createPeopleGroup::setParent);
        }

        PeopleGroup savedPeopleGroup = repository.save(createPeopleGroup);
        UniversalResponse universalResponse = groupAccessService.saveGroupAccess(UserRequestContext.getCurrentUserName(), GroupAccessRole.ADMIN, savedPeopleGroup);
        System.out.println(universalResponse);
        System.out.println(savedPeopleGroup);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedPeopleGroup);
        response.setStatusCode(201);
        return response;
    }
}
