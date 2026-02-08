package ke.co.myfuture.Myfuture.QuestionStore.SEO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class SitemapController {

    private final SitemapService sitemapService;

    public SitemapController(SitemapService sitemapService) {
        this.sitemapService = sitemapService;
    }

    @GetMapping(value = "/read/sitemap.xml", produces = "application/xml")
    public String sitemap() throws IOException {
        return sitemapService.getSitemapContent();
    }
}

