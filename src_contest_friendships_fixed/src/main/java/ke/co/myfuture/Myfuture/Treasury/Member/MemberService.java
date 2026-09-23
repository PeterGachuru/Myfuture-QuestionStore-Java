package ke.co.myfuture.Myfuture.Treasury.Member;

import ke.co.myfuture.Myfuture.Treasury.Person.Person;
import ke.co.myfuture.Myfuture.Treasury.Person.PersonRepository;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroupRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Service
public class MemberService {
    @Autowired
    MemberRepository repository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    PeopleGroupRepository peopleGroupRepository;
    public UniversalResponse saveMember(Long personId,
                                         Long groupId) {
        Optional<PeopleGroup> peopleGroup = peopleGroupRepository.findById(groupId);
        if (peopleGroup.isEmpty())
            return null;
        Optional<Person> person = personRepository.findById(personId);
        if (person.isEmpty())
            return null;
        Member member = new Member();
        member.setPerson(person.get());
        member.setPeopleGroup(peopleGroup.get());
        Member savedMember = repository.save(member);
        System.out.println(savedMember);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedMember);
        response.setStatusCode(201);
        return response;
    }

    public String[] getTargetType() {
        String[] targetTypes = new String[]{"anyhow", "pledge", "weekly", "monthly", "annual"};
        return targetTypes;
    }

}
