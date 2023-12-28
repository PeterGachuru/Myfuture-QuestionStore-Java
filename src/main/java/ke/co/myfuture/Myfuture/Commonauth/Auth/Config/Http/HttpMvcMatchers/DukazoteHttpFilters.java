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
                        .mvcMatchers("/productsubcategory/add").hasAnyAuthority(AccessRight.DUKA_CONFIGURE.toString())
                        .mvcMatchers("/productsubcategory/update").hasAnyAuthority(AccessRight.DUKA_CONFIGURE.toString())
                        .mvcMatchers("/productsubcategory/get/by/id").permitAll()
                        .mvcMatchers("/productsubcategory/all").permitAll()
                        .mvcMatchers("/product/add").hasAnyAuthority(AccessRight.DUKA_CONFIGURE.toString())
                        .mvcMatchers("/product/update").hasAnyAuthority(AccessRight.DUKA_CONFIGURE.toString())
                        .mvcMatchers("/product/get/by/id").permitAll()
                        .mvcMatchers("/product/all").permitAll()
                        .mvcMatchers("/inventory/add").hasAnyAuthority(AccessRight.DUKA_OPERATIONS.toString())
                        .mvcMatchers("/inventory/update").hasAnyAuthority(AccessRight.DUKA_CONFIGURE.toString())
                        .mvcMatchers("/inventory/get/by/id").permitAll()
                        .mvcMatchers("/inventory/all").permitAll()
                        .mvcMatchers("/cart/add").hasAnyAuthority(AccessRight.DUKA_OPERATIONS.toString())
                        .mvcMatchers("/cart/update").hasAnyAuthority(AccessRight.DUKA_CONFIGURE.toString())
                        .mvcMatchers("/cart/get/by/id").permitAll()
                        .mvcMatchers("/cart/all").permitAll()
                );
    }
}
