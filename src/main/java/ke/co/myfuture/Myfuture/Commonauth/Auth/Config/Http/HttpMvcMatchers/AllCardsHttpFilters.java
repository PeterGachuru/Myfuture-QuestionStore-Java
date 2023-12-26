package ke.co.myfuture.Myfuture.Commonauth.Auth.Config.Http.HttpMvcMatchers;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class AllCardsHttpFilters extends AbstractHttpConfigurer<AllCardsHttpFilters, HttpSecurity> {
    @Override
    public void init(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .mvcMatchers("/user/login").permitAll()
                        .anyRequest()
                        .denyAll()
                );
    }
}
