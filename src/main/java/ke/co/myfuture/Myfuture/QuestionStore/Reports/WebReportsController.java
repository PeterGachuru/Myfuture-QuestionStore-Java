package ke.co.myfuture.Myfuture.QuestionStore.Reports;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

@RestController
@RequestMapping("/read")
@CrossOrigin("*")
@Slf4j
public class WebReportsController {
    @Value("${report.logo}")
    String logo;
    @Value("${report.appname}")
    String appname;
    @Value("${report.path}")
    String path;

    @Value("${datasource.questions.url}")
    private String db;
    @Value("${datasource.questions.username}")
    private String username;
    @Value("${datasource.questions.password}")
    private String password;

    @Autowired
    ReportsService reportsService;


    @GetMapping("/pastpaper")
    public ResponseEntity<?> loadPdf(HttpServletRequest request, @RequestParam Long subject,
                                     @RequestParam Long classlevel) {
        String  userName = request.getHeader("userName");
        System.out.println("---------Test-----");
        System.out.println(userName);

        try {
            //List<ReportsDatabaseConn> loansList = loansRepo.findAllLoans();
//            if (!loansList.isEmpty()){
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/"+"question_paper.jrxml"));

            Map<String, Object> parameters = reportsService.setParameters(subject, classlevel);

            System.out.println(parameters);

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            System.out.println(report.getPages().size());
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=report.pdf");

            return ResponseEntity.ok().headers(headers).contentType(org.springframework.http.MediaType.APPLICATION_PDF).body(data);
//            }else {
//                return new ResponseEntity<>(new ResponseMessage("No Data Found", 404), HttpStatus.NOT_FOUND);
//            }
        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            exc.printStackTrace();
            return null;
        }
    }
}
