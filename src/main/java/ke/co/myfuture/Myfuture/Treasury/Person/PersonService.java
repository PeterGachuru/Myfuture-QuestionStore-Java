package ke.co.myfuture.Myfuture.Treasury.Person;

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
}
