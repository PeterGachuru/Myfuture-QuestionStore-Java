package ke.co.myfuture.Myfuture.NonJdbc;

import com.lowagie.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
public class PdfService {
    public byte[] convertHtmlToPdf(String htmlContent) throws DocumentException {
        String xhtml ="<!DOCTYPE html>" + htmlToXhtml(htmlContent);
        System.out.println("==================================Html to convert=================");
        System.out.println(xhtml);
        System.out.println("==================================================================");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(xhtml);
        renderer.layout();
        renderer.createPDF(outputStream, false);
        renderer.finishPDF();
        return outputStream.toByteArray();
    }
    private String htmlToXhtml(final String html) {
        final Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html();
    }
}