package ke.co.myfuture.Myfuture.Treasury.GroupAccess;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Treasury.Member.Member;
import ke.co.myfuture.Myfuture.Treasury.Member.MemberService;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.Person.PersonRepository;
import ke.co.myfuture.Myfuture.Treasury.Person.PersonService;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroupRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroupAccessService {
    @Autowired
    GroupAccessRepository repository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PersonService personService;

//    @Autowired
//    PersonRepository personRepository;
    @Autowired
    MemberService memberService;


    @Autowired
    PeopleGroupRepository peopleGroupRepository;
    public UniversalResponse saveGroupAccess(String username, GroupAccessRole role,
                                             PeopleGroup peopleGroup) {
        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty()){
            System.out.println("There is no such user");
            return null;
        }
        Optional<Person> person = null;
        if (peopleGroup.getParent() != null) {
            System.out.println("New group has a parent");
            person = personService.getPersonForGroup(username, peopleGroup.getParent());
        }
        else {
            person = personService.createPersonFromLoginUser(username, peopleGroup);
        }
        if (person.isEmpty()) {
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Error");
            response.setMessage("Unable to create you as a member");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            System.out.println("Person is empty");
            return response;
        }
        if (person.isPresent()) {
            memberService.saveMember(person.get().getId(), peopleGroup.getId());
        }

        GroupAccess groupAccess = new GroupAccess();
        groupAccess.setUsername(username);
        groupAccess.setRole(role);
        groupAccess.setPerson(person.get());
        groupAccess.setPeopleGroup(peopleGroup);
        groupAccess.setLoginUserId(userOptional.get().getId());
        GroupAccess savedGroupAccess = repository.save(groupAccess);
        System.out.println("Saved group access");
        System.out.println(savedGroupAccess);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedGroupAccess);
        response.setStatusCode(201);
        return response;
    }

    public UniversalResponse saveGroupAccess(GroupAccess groupAccess) {
        Optional<PeopleGroup> peopleGroup = peopleGroupRepository.findById(groupAccess.getGroupId());
        if (peopleGroup.isEmpty()) {
            System.out.println("No peopleGroup");
            return null;
        }
        groupAccess.setPeopleGroup(peopleGroup.get());
        Optional<User> user = userRepository.findByEmail(groupAccess.getUsername());
        if (user.isEmpty()) {
            System.out.println("No user");
            return null;
        }
        GroupAccess savedGroupAccess = repository.save(groupAccess);
        System.out.println(savedGroupAccess);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedGroupAccess);
        response.setStatusCode(201);
        return response;
    }

    public Optional<GroupAccess> findGroupAccess(Long userId, Long groupId) {
//        return null;
        return repository.findByUserIdAndGroupId(userId, groupId);
//        return Optional.empty();
    }
}
