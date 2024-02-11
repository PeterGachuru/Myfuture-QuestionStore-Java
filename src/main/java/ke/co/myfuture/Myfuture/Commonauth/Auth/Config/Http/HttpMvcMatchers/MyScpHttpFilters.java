package ke.co.myfuture.Myfuture.Commonauth.Auth.Config.Http.HttpMvcMatchers;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Role.AccessRight;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class MyScpHttpFilters  extends AbstractHttpConfigurer<MyScpHttpFilters, HttpSecurity> {
    @Override
    public void init(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .mvcMatchers("/api/files/upload").permitAll()
                        .mvcMatchers("/api/files/download").permitAll()
                );
    }
}
