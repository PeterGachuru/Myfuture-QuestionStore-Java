package ke.co.myfuture.Myfuture.Treasury.TextReport;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("treasury/textreport")
public class TextReportController {
    @Autowired
    TextReportRepository repository;

    @Autowired
    TextReportService accountService;

    @PostMapping("add")
    public ResponseEntity<?> newTextReport(@RequestBody TextReport textReport) {
        System.out.println("Report in: "+textReport);
        UniversalResponse response = accountService.saveTextReport(textReport);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateTextReport(@RequestBody TextReport account) {
        return new ResponseEntity<>(accountService.updateTextReport(account), HttpStatus.OK);
    }
    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchTextReport(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("TextReport retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("generate")
    public ResponseEntity<?> fetchReport(@RequestParam("id") Long id) {
        return new ResponseEntity<>(accountService.generateReport(repository.findById(id).get()), HttpStatus.OK);
    }
    @GetMapping("all")
    public ResponseEntity<?> fetchProductCategory(@RequestParam( name = "parentId") Long parentId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("ProductCategory retrieved Successfully");

        List<TextReport> accountList;
            accountList = repository.findAllByAuditTrails_DeletedFlag(false, parentId);
        response.setEntity(accountList);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
