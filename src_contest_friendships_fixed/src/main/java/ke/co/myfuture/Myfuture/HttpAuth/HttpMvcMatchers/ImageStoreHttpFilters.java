package ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class ImageStoreHttpFilters extends AbstractHttpConfigurer<ImageStoreHttpFilters, HttpSecurity> {

    @Override
    public void init(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth

                .requestMatchers("/images/all").permitAll()
                .requestMatchers("/images/upload").permitAll()
                .requestMatchers("/images/update").permitAll()
                .requestMatchers("/images/get").permitAll()
                .requestMatchers("/images/display/byid").permitAll()
                .requestMatchers("/images/display").permitAll()
        );
    }

    @Override
    public void configure(HttpSecurity http) {
        // required override for AbstractHttpConfigurer
    }
}