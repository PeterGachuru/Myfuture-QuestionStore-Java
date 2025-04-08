package ke.co.myfuture.Myfuture.Commonauth.Utils;

import ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails.SchedulerService;
import ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails.SenderService;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.*;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomMailSender {
    String propertiesFilePath = "mailconfigs/authmail.properties";

    @Autowired
    SchedulerService schedulerService;

    @Value("${custommail.sendurl}")
    private String sendEmailUrl;

    public Boolean scheduleImmediateMail(String toEmail, String subject, String emailContent, String fromName) {
        return schedulerService.persistScheduledEmail(toEmail, subject, emailContent, fromName, LocalDateTime.now().plusSeconds(1), SenderService.Groups);
    }

    public List<String> readFile(String path) {
        if (path == null) return null;
        try {
            // Read all lines from the file into a List of Strings
            return Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            // Handle any IOException that occurs during file reading
            e.printStackTrace();
        }
        return null;
    }

    public static String readFileAsString(String filePath) {
        if (filePath == null) return null;
        try {
            Path path = Paths.get(filePath);
            byte[] encodedBytes = Files.readAllBytes(path);
            return new String(encodedBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] readAsArray(String fileName) {
        List<String> lines = readFile(fileName);

        List<String> checked = new ArrayList<>();
        if (lines != null) {
            for (String line: lines) {
                if (!line.trim().isEmpty()) {
                    checked.add(line.trim());
                }
            }
        }
        return checked.toArray(new String[0]);
    }

    public HashMap<String, String> getProperties() {
        HashMap<String, String> properties= new HashMap<>();
        List<String> propertyLines = readFile(propertiesFilePath);
        if (propertyLines != null){
            for (String propertyLine: propertyLines) {
//                System.out.println(propertyLine);
                if (propertyLine.contains("=")) {
//                    System.out.println("contains");
                    String[] splits = propertyLine.split("=");
                    properties.put(splits[0].trim(), splits[1].trim());
                }
            }
        }
        return properties;
    }

    public HashMap<String, String> readArguments(String[] args) {
        HashMap<String, String> argsMap= new HashMap<>();
        if (args != null) {
            for (String arg: args) {
                System.out.println(arg);
                if (arg.contains("=")) {
//                    System.out.println("contains");
                    String[] splits = arg.split("=");
                    argsMap.put(splits[0].trim(), splits[1].trim());
                }
            }
        }
        return argsMap;
    }

    public Boolean sendEmail(String subject, String body, String[] toList, String[] ccList, String[] attachedFilePaths, String fromName) {
        System.out.println("Sending mail to "+toList[0]+", subject: "+subject);

        if (true) {
            return sendEmailOverRest(subject, body, toList,ccList, attachedFilePaths, fromName);
        }
        HashMap<String, String> properties = getProperties();
        String from = properties.get("spring.mail.username");
        String username = properties.get("spring.mail.username");
        String password = properties.get("spring.mail.password");
        // SMTP server properties
        Properties props = new Properties();
        props.setProperty("mail.smtp.ssl.trust", properties.get("spring.mail.host")); // Use specific host
        props.put("mail.smtp.auth", properties.get("spring.mail.properties.mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", properties.get("spring.mail.properties.mail.smtp.starttls.enable"));
        props.put("mail.smtp.host", properties.get("spring.mail.host"));
        props.put("mail.smtp.port", properties.get("spring.mail.port"));
        props.put("mail.debug", "false"); // Disable debug in production

        // Create a Session object with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a MimeMessage object
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, fromName));
            message.setSubject(subject); // Set the email subject
            message.setReplyTo(InternetAddress.parse(from));
            message.setHeader("Message-ID", "<" + UUID.randomUUID() + "@" + properties.get("spring.mail.host") + ">");
            message.setHeader("X-Mailer", "JavaMail");


            // Add recipients
            if (toList != null) {
                for (String email : toList) {
                    if (email.toLowerCase().contains("myfuture.co.ke"))
                        return false;
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                }
            }
            if (ccList != null) {
                for (String cc : ccList) {
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
                }
            }


            Multipart multipart = new MimeMultipart();
            MimeBodyPart plainTextPart = new MimeBodyPart();
            plainTextPart.setText(Jsoup.parse(body).text(), "utf-8");

// Create HTML content
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(body, "text/html; charset=utf-8");

// Add both parts to a multipart/alternative
            Multipart alternativeMultipart = new MimeMultipart("alternative");
            alternativeMultipart.addBodyPart(plainTextPart);
            alternativeMultipart.addBodyPart(htmlPart);

// Add alternative to the main multipart
            MimeBodyPart alternativePart = new MimeBodyPart();
            alternativePart.setContent(alternativeMultipart); // Set the multipart as content

// Add the alternative part to the main multipart
            multipart.addBodyPart(alternativePart);



            // Set the multipart object as the content of the message
            message.setContent(multipart);

            // Send the email
            Transport.send(message);
            System.out.println("Email sent successfully.");
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("End of email sending");
            return false;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean sendEmailOverRest(String subject, String body, String[] toList, String[] ccList, String[] attachedFilePaths, String fromName) {
        System.out.println("Sending mail via REST to " + toList[0] + ", subject: " + subject);

        // Load your SMTP configuration
        HashMap<String, String> properties = getProperties();

        // Prepare request payload
        EmailRequest requestPayload = new EmailRequest();
        requestPayload.subject = subject;
        requestPayload.body = body;
        requestPayload.toList = toList;
        requestPayload.ccList = ccList;
        requestPayload.attachedFilePaths = attachedFilePaths;
        requestPayload.fromName = fromName;
        requestPayload.mailConfig = properties;

        try {
            // Create REST client
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<EmailRequest> request = new HttpEntity<>(requestPayload, headers);

            // Make POST call to your local microservice
            ResponseEntity<Boolean> response = restTemplate.postForEntity(
                    sendEmailUrl, request, Boolean.class);

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
