package ke.co.myfuture.Myfuture.Treasury.Person;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.Utils.CustomMailSender;
import ke.co.myfuture.Myfuture.Treasury.Member.MemberService;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroupRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails.SchedulerService.isValidEmail;

@Service
public class PersonService {
    @Autowired
    PersonRepository personRepository;
    @Autowired
    PeopleGroupRepository peopleGroupRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CustomMailSender customMailSender;

    public UniversalResponse savePerson(Person account) {
        Optional<PeopleGroup> peopleGroup = peopleGroupRepository.findById(account.getInitialGroupId());
        if (peopleGroup.isEmpty())
            return null;
        Person savedPerson = personRepository.save(account);
        memberService.saveMember(savedPerson.getId(), peopleGroup.get().getId());
        System.out.println(savedPerson);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedPerson);
        response.setStatusCode(201);
        return response;
    }

    public UniversalResponse verifyPersonEmail(Long personId, Long groupId) {
        Optional<Person> person = personRepository.foundMemberById(false, groupId, personId);
        Optional<PeopleGroup> peopleGroup = peopleGroupRepository.findById(groupId);
        if (person.isEmpty()) {
            System.out.println("Person is empty");
            return null;
        }
        if (person.get().getEmail() == null || !isValidEmail(person.get().getEmail())) {
            System.out.println("Null email or email is invalid");
            return null;
        }

        System.out.println("About to generate email");
        UniversalResponse response = new UniversalResponse();
        if (sendVerificationEmail(person.get().getEmail(), person.get().getName(), peopleGroup.get().getName(),
                generateEmailVerificationLink(personId, groupId, person.get().getEmail()))
        ){
            System.out.println("email generation successful");
            person.get().setEmailVerificationAttemptStatus(VerificationStatus.SENT);
            Person savedPerson = personRepository.save(person.get());
            System.out.println(savedPerson);
            response.setStatus("Success");
            response.setMessage("Saved successfully");
            response.setEntity(savedPerson);
            response.setStatusCode(201);
        }
        return response;
    }

    public String generateEmailVerificationLink(Long memberId, Long groupId, String email) {
        String encodedEmail = Base64.getUrlEncoder().encodeToString(email.getBytes(StandardCharsets.UTF_8));
        return "https://groups.ibukatech.com/member/verify-email?personId=" + memberId + "&groupId=" + groupId + "&email=" + encodedEmail;
    }

    public Boolean sendVerificationEmail(String toEmail, String personName, String groupName, String verificationLink) {
        String subject = groupName+" - Verify Your Membership - Ibuka Groups";
        String fromName = "Ibuka Groups";

        String emailContent =
                "<html>\n" +
                        "<body>\n" +
                        "    <div style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; text-align: center;'>\n" +
                        "        <div style='background: #ffffff; padding: 20px; border-radius: 8px;'>\n" +
                        "            <h2 style='color: #333;'>Ibuka Groups</h2>\n" +
                        "            <p>Hello <strong>" + personName + "</strong>,</p>\n" +
                        "            <p>You have been listed as a member of the group <strong>" + groupName + "</strong>.</p>\n" +
                        "            <p>Click below to verify your membership:</p>\n" +
                        "            <a href='" + verificationLink + "' style='background: #28a745; color: #ffffff; padding: 12px; text-decoration: none; border-radius: 5px;'>\n" +
                        "                Verify Membership\n" +
                        "            </a>\n" +
                        "            <p>If you did not request this, ignore this email.</p>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "</html>";
        return customMailSender.scheduleImmediateMail(toEmail, subject, emailContent, fromName);
    }

    public Optional<Person> getPersonForGroup(String username, PeopleGroup peopleGroup) {
        return personRepository.findPersonByLoginUsernameAndGroupId(username, peopleGroup.getId());
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
        Person savedPerson = personRepository.save(person);

        return Optional.of(savedPerson);
    }

    public UniversalResponse executeVerifyPersonEmail(Long personId, Long groupId, String emailAddress) {
        Optional<Person> person = personRepository.foundMemberById(false, groupId, personId);
//        Optional<PeopleGroup> peopleGroup = peopleGroupRepository.findById(groupId);
        if (person.isEmpty()) {
            System.out.println("Returning because did not find person");
            return null;
        }
        if (person.get().getEmail() == null || !isValidEmail(person.get().getEmail())) {
            System.out.println("Returning because email is invalid");
            return null;
        }
        if (!person.get().getEmail().equals(emailAddress)) {
            System.out.println("Returning because email is invalid");
            return null;
        }
        if (person.get().getEmailVerified()) {
            System.out.println("Returning because email is already verified");
            return null;
        }

        UniversalResponse response = new UniversalResponse();

        person.get().setEmailVerificationAttemptStatus(VerificationStatus.SUCCESS);
        person.get().setEmailVerified(true);
        Person savedPerson = personRepository.save(person.get());
        System.out.println(savedPerson);
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedPerson);
        response.setStatusCode(201);
        return response;
    }
}