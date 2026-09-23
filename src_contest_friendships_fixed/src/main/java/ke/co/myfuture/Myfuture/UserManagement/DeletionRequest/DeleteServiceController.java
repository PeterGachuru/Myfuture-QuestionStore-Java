package ke.co.myfuture.Myfuture.UserManagement.DeletionRequest;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.NonJdbc.Migration.MigratorService;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contest;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/admin/userdelete")
@AllArgsConstructor
public class DeleteServiceController {

    @Autowired
    MigratorService migratorService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    IbukaStudentAccountRepository ibukaStudentAccountRepository;

    @Autowired
    ContestRepository contestRepository;


    @GetMapping
    public String deleteUser() {
        return "admin/submit-email";
    }


    @PostMapping("")
    public String deleteUser(
            @RequestParam("email") String email,
            Model model
    ) {

        Path deletionLogFile = createDeletionLogFile();

        logDeletion(deletionLogFile, "====================================================");
        logDeletion(deletionLogFile, "Deletion Request Started");
        logDeletion(deletionLogFile, "Time: " + LocalDateTime.now());
        logDeletion(deletionLogFile, "Requested Email: " + email);


        try {

            migratorService.execute(
                    "DELETE FROM statistics WHERE email = '" + email + "'"
            );

            logDeletion(deletionLogFile, "Deleted statistics records.");


            Optional<User> userOptional = userRepository.findByEmail(email);


            if (userOptional.isPresent()) {

                User user = userOptional.get();

                logDeletion(
                        deletionLogFile,
                        "User found. ID=" + user.getId()
                );


                String oldEmail = user.getEmail();
                String oldPhone = user.getPhoneNumber();


                String randomName =
                        "Deleted User " + randomString().substring(0, 8);

                String newEmail = randomEmail();
                String newPhone = randomPhone();


                user.setEmail(newEmail);
                user.setPhoneNumber(newPhone);

                user.setFirstName(randomName);
                user.setLastName(randomString().substring(0, 8));
                user.setFullName(randomName);

                user.setCounty(null);
                user.setPictureUrl(null);

                user.setResetPasswordToken(null);
                user.setResetPasswordTokenExpire(null);

                user.setAnonymized(true);
                user.setStatus("ANONYMIZED");


                userRepository.save(user);


                logDeletion(deletionLogFile, "User anonymized.");
                logDeletion(deletionLogFile, "Old Email: " + oldEmail);
                logDeletion(deletionLogFile, "New Email: " + newEmail);
                logDeletion(deletionLogFile, "Old Phone: " + oldPhone);
                logDeletion(deletionLogFile, "New Phone: " + newPhone);


                List<IbukaStudentAccount> students =
                        ibukaStudentAccountRepository.findByParent(user.getId());


                logDeletion(
                        deletionLogFile,
                        "Students found: " + students.size()
                );


                for (IbukaStudentAccount student : students) {

                    String first = "Deleted";
                    String last = randomString().substring(0, 8);


                    student.setFirstName(first);
                    student.setLastName(last);
                    student.setName(first + " " + last);
                    student.setParentUsername(newEmail);

                    student.setSchool(null);


                    ibukaStudentAccountRepository.save(student);


                    logDeletion(
                            deletionLogFile,
                            "Anonymized student ID=" + student.getId()
                    );


                    List<Contest> contests =
                            contestRepository.findByCreator(student);


                    logDeletion(
                            deletionLogFile,
                            "Student contests found: " + contests.size()
                    );


                    for (Contest contest : contests) {

                        contest.setCreatorName("Deleted User");

                        contestRepository.save(contest);


                        logDeletion(
                                deletionLogFile,
                                "Updated contest ID=" + contest.getId()
                        );
                    }
                }


                logDeletion(
                        deletionLogFile,
                        "Deletion completed successfully."
                );


            } else {

                logDeletion(
                        deletionLogFile,
                        "No user found with email."
                );
            }


        } catch (Exception e) {

            logDeletion(
                    deletionLogFile,
                    "ERROR: " + e.getMessage()
            );

            for (StackTraceElement element : e.getStackTrace()) {
                logDeletion(deletionLogFile, element.toString());
            }

            throw e;
        }


        model.addAttribute(
                "message",
                "User deleted successfully: " + email
        );


        return "admin/submit-email";
    }



    /**
     * Creates the deletion log file once.
     */
    private Path createDeletionLogFile() {

        try {

            Path folder =
                    Paths.get(
                            System.getProperty("user.dir"),
                            "deletions"
                    );


            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }


            String filename =
                    LocalDateTime.now()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "yyyy-MM-dd_HH-mm-ss"
                                    )
                            )
                            + ".log";


            Path file = folder.resolve(filename);


            Files.createFile(file);


            return file;


        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to create deletion log file",
                    e
            );
        }
    }



    /**
     * Appends message to the same deletion file.
     */
    private void logDeletion(Path file, String message) {

        try {

            Files.write(
                    file,
                    (
                            LocalDateTime.now()
                                    + " : "
                                    + message
                                    + System.lineSeparator()
                    ).getBytes(StandardCharsets.UTF_8),

                    StandardOpenOption.APPEND
            );


        } catch (IOException e) {

            e.printStackTrace();
        }
    }



    private String randomString() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "");
    }


    private String randomEmail() {
        return randomString().substring(0, 12)
                + "@deleted.ibuka";
    }


    /**
     * Non-real phone number.
     * Prevents accidental SMS/calls.
     */
    private String randomPhone() {
        return "DELETED-"
                + randomString().substring(0, 12);
    }
}