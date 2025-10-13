package ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IbukaStudentAccountService {

    @Autowired
    IbukaStudentAccountRepository ibukaStudentAccountRepository;

    public boolean isCodeInUse(String code) {
        Optional<IbukaStudentAccount> ibukaStudentAccountOptional = ibukaStudentAccountRepository.findByShareCode(code);
        return ibukaStudentAccountOptional.isPresent();
    }


    public String generateUniqueCode(String firstName, String lastName) {
        List<String> candidates = new ArrayList<>();

        firstName = (firstName == null ? "" : firstName.trim().toLowerCase());
        lastName = (lastName == null ? "" : lastName.trim().toLowerCase());

        // Generate candidate codes (must be >= 4 chars)
        if (!firstName.isEmpty()) {
            if (firstName.length() >= 4) candidates.add(firstName);
        }
        if (!lastName.isEmpty()) {
            if (lastName.length() >= 4) candidates.add(lastName);
        }
        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            String combined1 = firstName + lastName;
            String combined2 = lastName + firstName;
            if (combined1.length() >= 4) candidates.add(combined1);
            if (combined2.length() >= 4) candidates.add(combined2);

            // Mix first letters + full name
            String mixed1 = firstName.substring(0, 1) + lastName;
            String mixed2 = lastName.substring(0, 1) + firstName;
            if (mixed1.length() >= 4) candidates.add(mixed1);
            if (mixed2.length() >= 4) candidates.add(mixed2);

            // Take first 2 + first 2
            if (firstName.length() >= 2 && lastName.length() >= 2) {
                String shortMix = firstName.substring(0, 2) + lastName.substring(0, 2);
                if (shortMix.length() >= 4) candidates.add(shortMix);
            }
        }

        // Ensure at least one candidate exists
        if (candidates.isEmpty()) {
            candidates.add("stud"); // default fallback
        }

        // Try candidates directly
        for (String candidate : candidates) {
            if (!isCodeInUse(candidate)) {
                return candidate;
            }
        }

        int num = 1;
        while (true) {
            for (String candidate : candidates) {
                String withNum = candidate+num;
                if (withNum.length() >= 4 && !isCodeInUse(withNum)) {
                    return withNum;
                }
            }
            num++;
        }
    }

    public String generateUniqueCode(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return generateUniqueCode("", "");
        }

        String[] parts = fullName.trim().split("\\s+");
        String firstName, lastName;

        if (parts.length >= 2) {
            // Take the first two words
            firstName = parts[0];
            lastName = parts[1];
        } else {
            // Only one word → lastName is empty
            firstName = parts[0];
            lastName = "";
        }

        return generateUniqueCode(firstName, lastName);
    }

    public List<IbukaStudentAccount> findByParent(Long parentId) {
        List<IbukaStudentAccount> studentAccounts = ibukaStudentAccountRepository.findByParent(parentId);

        boolean changed = false;
        String code;
        for (IbukaStudentAccount studentAccount: studentAccounts) {
            if (studentAccount.getShareCode() == null) {
                code = generateUniqueCode(studentAccount.getName());
                studentAccount.setShareCode(code);
                changed = true;
                ibukaStudentAccountRepository.save(studentAccount);
            }
        }

        return ibukaStudentAccountRepository.findByParent(parentId);
    }
}
