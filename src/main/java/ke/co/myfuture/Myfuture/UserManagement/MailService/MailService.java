package ke.co.myfuture.Myfuture.UserManagement.MailService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
@Slf4j
public class MailService {
    @Value("${organisation.company_logo_path}")
    private String company_logo_path;
    @Value("${organisation.image_banner}")
    private String banner_path;
    @Value("${organisation.from_mail}")
    private String fromEmail;

    @Value("${organisation.emailSalutation}")
    private String emailSalutation;
    @Value("${organisation.emailMessage}")
    private String emailMessage;
    @Value("${organisation.emailRemarks}")
    private String emailRemarks;
    @Value("${organisation.emailRegards}")
    private String emailRegards;
    @Value("${organisation.emailOrganizationName}")
    private String emailOrganizationName;
    @Value("${organisation.emailOrganizationPhone}")
    private String emailOrganizationPhone;
    @Value("${organisation.emailOrganizationMail}")
    private String emailOrganizationMail;
    @Value("${organisation.emailOrganizationWebsite}")
    private String emailOrganizationWebsite;

    @Autowired
    JavaMailSender javaMailSender;

    public Boolean sendEmail(String to, String message, String subject) {
        try{
            System.out.println("----------------------------------------------------------------");
            System.out.println("Email is enabled!");
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setFrom(fromEmail);
            helper.setSubject(subject);
            helper.setText(
                    "<!DOCTYPE html>\n" +
                            "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n" +
                            "<head>\n" +
                            "  <meta charset=\"utf-8\">\n" +
                            "  <meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">\n" +
                            "  <meta name=\"x-apple-disable-message-reformatting\">\n" +
                            "  <title></title>\n" +
                            "  <!--[if mso]>\n" +
                            "  <style>\n" +
                            "    table {border-collapse:collapse;border-spacing:0;border:none;margin:0; margin-top:10px;margin-bottom:10px;}\n" +
                            "    div, td {padding:0;}\n" +
                            "    div {margin:0 !important;}\n" +
                            "  </style>\n" +
                            "  <noscript>\n" +
                            "    <xml>\n" +
                            "      <o:OfficeDocumentSettings>\n" +
                            "        <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
                            "      </o:OfficeDocumentSettings>\n" +
                            "    </xml>\n" +
                            "  </noscript>\n" +
                            "  <![endif]-->\n" +
                            "  <style>\n" +
                            "    table, td, div, h1, p {\n" +
                            "      font-family: Arial, sans-serif;\\n\"\n" +
                            "    }\n" +
                            "    @media screen and (max-width: 530px) {\n" +
                            "      .unsub {\n" +
                            "        display: block;\n" +
                            "        padding: 8px;\n" +
                            "        margin-top: 14px;\n" +
                            "        border-radius: 6px;\n" +
                            "        background-color: #555555;\n" +
                            "        text-decoration: none !important;\n" +
                            "        font-weight: bold;\n" +
                            "      }\n" +
                            "      .col-lge {\n" +
                            "        max-width: 100% !important;\n" +
                            "      }\n" +
                            "    }\n" +
                            "    @media screen and (min-width: 531px) {\n" +
                            "      .col-sml {\n" +
                            "        max-width: 27% !important;\n" +
                            "      }\n" +
                            "      .col-lge {\n" +
                            "        max-width: 73% !important;\n" +
                            "      }\n" +
                            "    }\n" +
                            "  </style>\n" +
                            "</head>\n" +
                            "<body style=\" margin-top:10px; margin-bottom:10px; margin:0;padding:0;word-spacing:normal;background-color: #566fff;\">\n" +
//                            "  <div role=\"article\" aria-roledescription=\"email\" lang=\"en\" style=\"text-size-adjust:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;background-color:#939297;\">\n" +
                            "  <div role=\"article\" aria-roledescription=\"email\" lang=\"en\" style=\"text-size-adjust:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;background-color:#566fff;\">\n" +
                            "    <table role=\"presentation\" style=\"width:100%; padding-top: 10px; padding-bottom: 10px; border:none;border-spacing:0;\">\n" +
                            "      <tr>\n" +
                            "        <td align=\"center\" style=\"padding:0;\">\n" +
                            "          <!--[if mso]>\n" +
                            "          <table role=\"presentation\" align=\"center\" style=\"width:600px; margin-top: 10px; margin-bottom: 10px;\">\n" +
                            "          <tr>\n" +
                            "          <td>\n" +
                            "          <![endif]-->\n" +
                            "          <table role=\"presentation\" style=\"width:94%;max-width:600px;border:none;border-spacing:0;text-align:left;font-family:Arial,sans-serif;font-size:16px;line-height:22px;color:#363636;\">\n" +
                            "              <td style=\"padding:5px;text-align:center;font-size:12px;background-color:#ffffff;\">\n" +
                            "                <a href=\"http://www.example.com/\" style=\"text-decoration:none;\"><img src='cid:companyLogo' alt=\"Logo\" style=\"width:20%; text-align:center; margin:auto; height:auto;border:none;text-decoration:none;color:#ffffff;\"></a>\n" +
                            "                <hr>\n" +
                            "              </td>\n" +
                            "            <tr>\n" +
                            "              <td style=\"padding:30px;background-color:#ffffff;\">\n" +
                            "                 <h1 style=\"margin-top:0;margin-bottom:16px;font-size:26px;line-height:32px;font-weight:bold;letter-spacing:-0.02em;\">" + emailSalutation + "</h1>\n" +
                            "                    </p>\n" +
                            "                   <p style=\"margin:0;\">\n" + message + "</p>\n" +
                            "                   <p style=\"margin:0;\">\n" + emailRemarks + "\n" +
                            "                    </p>\n" +
                            "                   <br>\n" +
                            "                   <br>\n" +
                            "                   <br>\n" +
                            "                   <p style=\"margin:0;\">\n" + emailRegards + "\n" +
                            "                    </p>\n" +
                            "                   <p style=\"margin:0;\">\n" + emailOrganizationName + "\n" +
                            "                    </p>\n" +
                            "                   <p style=\"margin:0;\">\n" + "<b>Tel/Phone: </b> " + emailOrganizationPhone + "\n" +
                            "                    </p>\n" +
                            "                   <p style=\"margin:0;\">\n" + "<b>Email: </b> " + emailOrganizationMail + "\n" +
                            "                    </p>\n" +
                            "                   <p style=\"margin:0;\">\n" + "<b>Website: </b> " + emailOrganizationWebsite + "\n" +
                            "                    </p>\n" +
                            "              </td>\n" +
                            "            </tr>\n" +
                            "              <td style=\"padding:0px; margin-bottom: 0px;text-align:center;font-size:12px;background-color:#ffffff;\">\n" +
                            "                       <img src='cid:rightSideImage' style='width:100%;'/>" +
                            "              </td>\n" +
                            "              <td style=\"padding:0px; margin-bottom: 0px;text-align:center;font-size:12px;background-color:#ffffff;\">\n" +
                            "              </td>\n" +
                            "            <tr>\n" +
                            "            </tr>\n" +
                            "           \n" +
                            "            <tr>\n" +
                            "              <td style=\"padding:30px;text-align:center;font-size:12px;background-color:#001c27;color:#cccccc;\">\n" +
                            "              <p style=\"margin:0;font-size:14px;line-height:20px;\">&reg; copyright 2021<br></p>\n" +
                            "              </td>\n" +
                            "            </tr>\n" +
                            "          </table>\n" +
                            "          <!--[if mso]>\n" +
                            "          </td>\n" +
                            "          </tr>\n" +
                            "          </table>\n" +
                            "          <![endif]-->\n" +
                            "        </td>\n" +
                            "      </tr>\n" +
                            "    </table>\n" +
                            "  </div>\n" +
                            "</body>\n" +
                            "</html>", true);
            helper.addInline("companyLogo",
                    new File(company_logo_path));
            helper.addInline("rightSideImage",
                    new File(banner_path));
            javaMailSender.send(mimeMessage);

            System.out.println("Mail sent successfully to: " + to);
            log.info("Sent successfully,sent to: {}", to);
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
