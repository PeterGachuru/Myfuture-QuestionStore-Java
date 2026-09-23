package ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers;

import ke.co.myfuture.Myfuture.Commonauth.Auth.RoleConfig.AccessRight;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class DukazoteHttpFilters extends AbstractHttpConfigurer<DukazoteHttpFilters, HttpSecurity> {

    @Override
    public void init(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth

                // Product Category
                .requestMatchers("/productcategory/add")
                .hasAuthority(AccessRight.DUKA_CONFIGURE.name())

                .requestMatchers("/productcategory/update")
                .hasAuthority(AccessRight.DUKA_CONFIGURE.name())

                .requestMatchers("/productcategory/get/by/id")
                .permitAll()

                .requestMatchers("/productcategory/all")
                .permitAll()

                // Product Subcategory
                .requestMatchers("/productsubcategory/add")
                .hasAuthority(AccessRight.DUKA_CONFIGURE.name())

                .requestMatchers("/productsubcategory/update")
                .hasAuthority(AccessRight.DUKA_CONFIGURE.name())

                .requestMatchers("/productsubcategory/get/by/id")
                .permitAll()

                .requestMatchers("/productsubcategory/all")
                .permitAll()

                // Products
                .requestMatchers("/product/add")
                .hasAuthority(AccessRight.DUKA_CONFIGURE.name())

                .requestMatchers("/product/update")
                .hasAuthority(AccessRight.DUKA_CONFIGURE.name())

                .requestMatchers("/product/get/by/id")
                .permitAll()

                .requestMatchers("/product/all")
                .permitAll()

                // Inventory
                .requestMatchers("/inventory/add")
                .hasAuthority(AccessRight.DUKA_OPERATIONS.name())

                .requestMatchers("/inventory/update")
                .hasAuthority(AccessRight.DUKA_CONFIGURE.name())

                .requestMatchers("/inventory/get/by/id")
                .permitAll()

                .requestMatchers("/inventory/all")
                .permitAll()

                // Cart
                .requestMatchers("/cart/add")
                .hasAuthority(AccessRight.DUKA_OPERATIONS.name())

                .requestMatchers("/cart/update")
                .hasAuthority(AccessRight.DUKA_CONFIGURE.name())

                .requestMatchers("/cart/get/by/id")
                .permitAll()

                .requestMatchers("/cart/all")
                .permitAll()

                // Reports
                .requestMatchers("/dukazote/reports/load")
                .permitAll()
        );
    }

    @Override
    public void configure(HttpSecurity http) {
        // required override for AbstractHttpConfigurer
    }
}