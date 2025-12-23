package ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class ImageStoreHttpFilters extends AbstractHttpConfigurer<MyScpHttpFilters, HttpSecurity> {
    @Override
    public void init(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .mvcMatchers("/images/all").permitAll()
                        .mvcMatchers("/images/upload").permitAll()
                        .mvcMatchers("/images/update").permitAll()
                        .mvcMatchers("/images/get").permitAll()
                        .mvcMatchers("/images/display/byid").permitAll()
                        .mvcMatchers("/images/display").permitAll()
                );
    }
}
