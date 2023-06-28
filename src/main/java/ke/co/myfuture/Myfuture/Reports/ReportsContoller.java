package ke.co.myfuture.Myfuture.Reports;

import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ReportsContoller {
    @Value("${report.logo}")
    String logo;
    @Value("${report.appname}")
    String appname;
    @Value("${report.path}")
    String path;

    @Value("${spring.datasource.url}")
    private String db;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;


    @GetMapping("/load")
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

            Map<String, Object> parameters = setParameters(subject, classlevel);

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
            return null;
        }
    }
    private Map<String, Object> setParameters(Long subject, Long classlevel) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("logo", logo);
        parameters.put("app_name", appname);
        parameters.put("query", "select *, char(row_num+64) AS num from (select ROW_NUMBER() OVER(PARTITION BY" +
                " cq.string) AS row_num, cq.string, ct.name, cnc.value, cq.qn_num  " +
                "from (select *, ROW_NUMBER() OVER(PARTITION BY '') AS qn_num from  " +
                "(select * from curri_question  where subtopic in " +
                "(select id from curri_topic where subject = "+subject+" and curri_level in " +
                "(select cl.id from  curri_level cl join curri_level scl " +
                "where cl.numbering > scl.numbering-3 and cl.numbering <= scl.numbering " +
                "and scl.curriculum = cl.curriculum and scl.id = "+classlevel+" )) order by rand() limit 100) as k) cq " +
                "join curri_topic ct on cq.subtopic = ct.id  join curri_normal_choice cnc on  cnc.question = cq.id  " +
                "order by cq.id) As k  order by qn_num;");
        return parameters;
    }
}
