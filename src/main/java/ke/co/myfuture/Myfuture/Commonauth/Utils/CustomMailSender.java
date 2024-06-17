package ke.co.myfuture.Myfuture.Commonauth.Utils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CustomMailSender {
    String propertiesFilePath = "application.properties";
    public CustomMailSender(String propertiesFilePath) {
        this.propertiesFilePath = propertiesFilePath;
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
                System.out.println(propertyLine);
                if (propertyLine.contains("=")) {
                    System.out.println("contains");
                    String[] splits = propertyLine.split("=");
                    properties.put(splits[0].trim(), splits[1].trim());
                }
            }
        }
        return properties;
    }

    public HashMap<String, String> readArguments(String[] args) {
        HashMap<String, String> argsMap= new HashMap<>();
        if (args != null){
            for (String arg: args) {
                System.out.println(arg);
                if (arg.contains("=")) {
                    System.out.println("contains");
                    String[] splits = arg.split("=");
                    argsMap.put(splits[0].trim(), splits[1].trim());
                }
            }
        }
        return argsMap;
    }

    public void sendEmail(String subject, String body, String[] toList, String[] ccList, String[] attachedFilePaths) {
        HashMap<String, String> properties = getProperties();
        String from =  properties.get("spring.mail.username");
        // Recipient's email address
        // Sender's Gmail username
        String username = properties.get("spring.mail.username");
        // Sender's Gmail password
        String password = properties.get("spring.mail.password");

        // SMTP server properties
        Properties props = new Properties();
        props.setProperty("mail.smtp.ssl.trust", "*");
        props.put("mail.smtp.auth", properties.get("spring.mail.properties.mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", properties.get("spring.mail.properties.mail.smtp.starttls.enable"));
        props.put("mail.smtp.host", properties.get("spring.mail.host"));
        props.setProperty("mail.debug", properties.get("mail.debug"));
        props.put("mail.smtp.port", properties.get("spring.mail.port"));

        // Create a Session object with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a MimeMessage object
            Message message = new MimeMessage(session);
            // Set the sender address
            message.setFrom(new InternetAddress(from));
            // Set the recipient address
            // Set the email subject
            message.setSubject(subject);
            // Set the email content
//            message.setText("This is a test email sent from JavaMail API.");

            if (toList !=null)
                for (String email : toList) {
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                }

            if (ccList !=null)
                for (String cc : ccList) {
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
                }

            // Create a multipart message
            Multipart multipart = new MimeMultipart();

            // Add message text
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            multipart.addBodyPart(messageBodyPart);

            if (toList !=null)
                for (String fileName : attachedFilePaths) {
                    messageBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(fileName);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(fileName);
                    multipart.addBodyPart(messageBodyPart);
                }

            // Set the multipart object as the content of the message
            message.setContent(multipart);

            // Send the email
            Transport.send(message);

            System.out.println("Email sent successfully.");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
