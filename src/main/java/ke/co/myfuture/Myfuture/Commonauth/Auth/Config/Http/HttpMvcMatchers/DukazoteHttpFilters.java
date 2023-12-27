package ke.co.myfuture.Myfuture.Commonauth.Auth.Config.Http.HttpMvcMatchers;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Role.AccessRight;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class DukazoteHttpFilters extends AbstractHttpConfigurer<AllCardsHttpFilters, HttpSecurity> {
    @Override
    public void init(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .mvcMatchers("/productcategory/add").hasAnyAuthority(AccessRight.DUKA_CONFIGURE.toString())
                        .mvcMatchers("/productcategory/update").hasAnyAuthority(AccessRight.DUKA_CONFIGURE.toString())
                        .mvcMatchers("/productcategory/get/by/id").permitAll()
                        .mvcMatchers("/productcategory/all").permitAll()
                );
    }
}
