package ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class TreasuryHttpFilters extends AbstractHttpConfigurer<TreasuryHttpFilters, HttpSecurity> {

    @Override
    public void init(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> {

            // All treasury endpoints require authentication
            auth.requestMatchers("/treasury/**").authenticated();

            // -------------------------------------------------------
            // Below is your future fine-grained rules (currently disabled)
            // Updated to Spring Security 6 style for when you enable them
            // -------------------------------------------------------

            /*
            auth.requestMatchers("/treasury/person/add")
                    .hasAuthority(AccessRight.CREATE_PERSON.name())

                .requestMatchers("/treasury/person/update")
                    .hasAuthority(AccessRight.CREATE_PERSON.name())

                .requestMatchers("/treasury/person/get/by/id")
                    .hasAuthority(AccessRight.VIEW_PERSON.name())

                .requestMatchers("/treasury/person/all")
                    .hasAuthority(AccessRight.VIEW_PERSON.name())

                .requestMatchers("/treasury/people-group/add")
                    .hasAuthority(AccessRight.CREATE_PEOPLE_GROUP.name())

                .requestMatchers("/treasury/people-group/update")
                    .hasAuthority(AccessRight.CREATE_PEOPLE_GROUP.name())

                .requestMatchers("/treasury/people-group/get/by/id")
                    .hasAuthority(AccessRight.VIEW_PEOPLE_GROUP.name())

                .requestMatchers("/treasury/people-group/all")
                    .hasAuthority(AccessRight.VIEW_PEOPLE_GROUP.name())

                .requestMatchers("/treasury/textreport/add")
                    .hasAuthority(AccessRight.CREATE_FUNDING_COMPAIGN.name())

                .requestMatchers("/treasury/textreport/update")
                    .hasAuthority(AccessRight.CREATE_FUNDING_COMPAIGN.name())

                .requestMatchers("/treasury/textreport/get/by/id")
                    .hasAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.name())

                .requestMatchers("/treasury/textreport/all")
                    .hasAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.name())

                .requestMatchers("/treasury/textreport/generate")
                    .hasAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.name())

                .requestMatchers("/treasury/contribution-plan/add")
                    .hasAuthority(AccessRight.CREATE_FUNDING_COMPAIGN.name())

                .requestMatchers("/treasury/contribution-plan/update")
                    .hasAuthority(AccessRight.CREATE_FUNDING_COMPAIGN.name())

                .requestMatchers("/treasury/contribution-plan/all")
                    .hasAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.name())

                .requestMatchers("/treasury/contribution-plan/get/by/id")
                    .hasAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.name())

                .requestMatchers("/treasury/account/get/by/id")
                    .hasAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.name())

                .requestMatchers("/treasury/account/all")
                    .hasAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.name())

                .requestMatchers("/treasury/account/add")
                    .hasAuthority(AccessRight.TRANSACT.name())

                .requestMatchers("/treasury/account/update")
                    .hasAuthority(AccessRight.TRANSACT.name())

                .requestMatchers("/treasury/transaction/add")
                    .hasAuthority(AccessRight.TRANSACT.name())

                .requestMatchers("/treasury/transaction/update")
                    .hasAuthority(AccessRight.TRANSACT.name())

                .requestMatchers("/treasury/transaction/get/by/id")
                    .hasAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.name())

                .requestMatchers("/treasury/transaction/all")
                    .hasAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.name());
            */
        });
    }

    @Override
    public void configure(HttpSecurity http) {
        // required override for AbstractHttpConfigurer
    }
}