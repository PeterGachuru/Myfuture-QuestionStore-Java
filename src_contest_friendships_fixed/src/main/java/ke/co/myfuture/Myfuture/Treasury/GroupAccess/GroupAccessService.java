package ke.co.myfuture.Myfuture.Treasury.GroupAccess;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.UserCreateRequest;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.AuthEntityResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserService;
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
    PersonRepository personRepository;

    @Autowired
    UserService userService;


    @Autowired
    PeopleGroupRepository peopleGroupRepository;
    public UniversalResponse saveGroupAccess(String username, GroupAccessRole role,
                                             PeopleGroup peopleGroup) {
        System.out.println("Username: "+username);
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


        System.out.println("Person to save for group: "+person.get());
        GroupAccess groupAccess = new GroupAccess();
        groupAccess.setUsername(username);
        groupAccess.setRole(role);
        groupAccess.setPerson(person.get());
        groupAccess.setPeopleGroup(peopleGroup);
        groupAccess.setLoginUserId(userOptional.get().getId());

        System.out.println("Group access to save: "+groupAccess);
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
        Optional<Person> person = personRepository.findById(groupAccess.getPersonId());
        if (person.isEmpty())
        {
            System.out.println("Person id is empty");
            return null;
        }
        if (!person.get().getEmailVerified()) {
            return new UniversalResponse(400, "Unverified email. Verify member email first");
        }

        if (!person.get().getEmail().toLowerCase().equals(groupAccess.getUsername().toLowerCase())){
            return new UniversalResponse(400, "Email matching that registered in membership");
        }

        groupAccess.setPeopleGroup(peopleGroup.get());
        Optional<User> user = userRepository.findByEmail(groupAccess.getUsername());
        if (user.isEmpty()) {
            System.out.println("User not created for login");
            createUser(person.get());
            user = userRepository.findByEmail(groupAccess.getUsername());
        }

        if (user.isEmpty()) {
            System.out.println("User not created for login");
            return new UniversalResponse(400, "User not created for login");
        }
        groupAccess.setLoginUserId(user.get().getId());
        groupAccess.setPerson(person.get());
        GroupAccess savedGroupAccess = repository.save(groupAccess);
        System.out.println(savedGroupAccess);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedGroupAccess);
        response.setStatusCode(201);
        return response;
    }

    private Boolean createUser(Person person) {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail(person.getEmail());
        userCreateRequest.setFirstName(getFirstName(person.getName()));
        userCreateRequest.setLastName(getLastName(person.getName()));
        userCreateRequest.setPhone(person.getPhoneNumber());
        userCreateRequest.setRole("ROLE_SUPERUSER");

        AuthEntityResponse authEntityResponse = userService.createUser(userCreateRequest);
        if (authEntityResponse.getStatusCode() < 400){
            return true;
        }
        return false;
    }

    public static String getFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 0 ? parts[0] : "";
    }

    public static String getLastName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : "";
    }

    public Optional<GroupAccess> findGroupAccess(String emailAddress, Long groupId) {
//        return null;
        System.out.println("findGroupAccess");
        System.out.println(emailAddress);
        System.out.println(groupId);
        return repository.findByUserIdAndGroupId(emailAddress, groupId);
//        return Optional.empty();
    }
}
