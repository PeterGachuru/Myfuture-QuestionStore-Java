package ke.co.myfuture.Myfuture.Treasury.Person;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Treasury.Member.MemberService;
import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.Person.PersonRepository;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroupRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonService {
    @Autowired
    PersonRepository repository;
    @Autowired
    PeopleGroupRepository peopleGroupRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    UserRepository userRepository;

    public UniversalResponse savePerson(Person account) {
        Optional<PeopleGroup> peopleGroup = peopleGroupRepository.findById(account.getInitialGroupId());
        if (peopleGroup.isEmpty())
            return null;
        Person savedPerson = repository.save(account);
        memberService.saveMember(savedPerson.getId(), peopleGroup.get().getId());
        System.out.println(savedPerson);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedPerson);
        response.setStatusCode(201);
        return response;
    }

    public Optional<Person> getPersonForGroup(String username, PeopleGroup peopleGroup) {
        return repository.findPersonByLoginUsernameAndGroupId(username, peopleGroup.getId());
    }

    public Optional<Person> createPersonFromLoginUser(String username, PeopleGroup peopleGroup) {
        System.out.println("createPersonFromLoginUser");
        System.out.println("Logged user: "+username);
        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty()) {
            System.out.println("Seems you are not logged in");
            return Optional.empty();
        }
        System.out.println("About to create a person");
        User user = userOptional.get();
        Person person = new Person();
        person.setName(user.getFirstName()+" "+user.getLastName());
        person.setEmail(user.getEmail());
        person.setInitialGroupId(peopleGroup.getId());
        person.setPhoneNumber(user.getPhoneNumber());
        Person savedPerson = repository.save(person);

        return Optional.of(savedPerson);
    }
}

