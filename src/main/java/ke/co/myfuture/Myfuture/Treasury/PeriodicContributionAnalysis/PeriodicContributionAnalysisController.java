package ke.co.myfuture.Myfuture.Treasury.PeriodicContributionAnalysis;

import com.lowagie.text.DocumentException;
import ke.co.myfuture.Myfuture.NonJdbc.PdfService;
import ke.co.myfuture.Myfuture.Treasury.TextReport.TextReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("treasury/periodic")
public class PeriodicContributionAnalysisController {
    @Autowired
    PeriodicContributionAnalysisService service;

    @Autowired
    PeriodicContributionAnalysisRepository repository;

    @Autowired
    PdfService pdfService;

    @PutMapping("calculate")
    public ResponseEntity<?> updateTextReport(@RequestParam("accountId") Long accountId) {
        return new ResponseEntity<>(service.calculate(accountId), HttpStatus.OK);
    }
    @GetMapping("report")
    public ResponseEntity<?> allForPlan(@RequestParam("planId") Long planId) {
        return new ResponseEntity<>(repository.getAllForPlan(planId), HttpStatus.OK);
    }

    @GetMapping("generatepdf")
    public ResponseEntity<byte[]>  generatePdfFile(@RequestParam("planId") Long planId) throws IOException, DocumentException {
        String contentToGenerate = service.toHtmlReport(planId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.setContentDispositionFormData("inline", "PrivacyPolicy.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(pdfService.convertHtmlToPdf("<div>"+
                contentToGenerate+"</div>"),
                headers, HttpStatus.OK);
        return response;
    }
}
