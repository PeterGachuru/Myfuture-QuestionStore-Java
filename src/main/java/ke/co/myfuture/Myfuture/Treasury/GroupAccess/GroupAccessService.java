package ke.co.myfuture.Myfuture.Treasury.GroupAccess;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroupRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroupAccessService {
    @Autowired
    GroupAccessRepository repository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PeopleGroupRepository peopleGroupRepository;
    public UniversalResponse saveGroupAccess(String username,
                                        Long groupId) {
        Optional<PeopleGroup> peopleGroup = peopleGroupRepository.findById(groupId);
        if (peopleGroup.isEmpty())
            return null;
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty())
            return null;
        GroupAccess groupAccess = new GroupAccess();
        groupAccess.setUsername(username);
        groupAccess.setPeopleGroup(peopleGroup.get());
        GroupAccess savedGroupAccess = repository.save(groupAccess);
        System.out.println(savedGroupAccess);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedGroupAccess);
        response.setStatusCode(201);
        return response;
    }
}
