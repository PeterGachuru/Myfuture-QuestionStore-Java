package ke.co.myfuture.Myfuture.QuestionStore.Utilities;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
public class SwaggerConfig {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private ApiInfo apiInfo() {
        return new ApiInfo("Myfuture",
                "Educational system.",
                "1.0",
                "Terms of service",
                new Contact("Myfuture", "www.dukazote.co.ke", "info@myfuture.co.ke"),
                "License of API",
                "API license URL",
                Collections.emptyList());
    }

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .apiInfo(apiInfo());
    }
}
