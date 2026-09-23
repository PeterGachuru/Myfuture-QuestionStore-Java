package ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class MyScpHttpFilters extends AbstractHttpConfigurer<MyScpHttpFilters, HttpSecurity> {

    @Override
    public void init(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth

                .requestMatchers("/api/files/upload").permitAll()
                .requestMatchers("/api/files/download").permitAll()
        );
    }

    @Override
    public void configure(HttpSecurity http) {
        // required override for AbstractHttpConfigurer
    }
}