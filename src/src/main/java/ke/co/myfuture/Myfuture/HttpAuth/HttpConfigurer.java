package ke.co.myfuture.Myfuture.HttpAuth;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.AuthTokenFilter;
import ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers.MyScpHttpFilters;
import ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers.AuthenticationHttpFilters;
import ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers.DukazoteHttpFilters;
import ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers.ImageStoreHttpFilters;
import ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers.MyfutureNavigationHttpFilters;
import ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers.TreasuryHttpFilters;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class HttpConfigurer {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final SecurityContextRepository securityContextRepository;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
//                .with(new MyfutureNavigationHttpFilters(), customizer -> {})
                .with(new DukazoteHttpFilters(), customizer -> {})
                .with(new MyScpHttpFilters(), customizer -> {})
                .with(new ImageStoreHttpFilters(), customizer -> {})
                .with(new TreasuryHttpFilters(), customizer -> {})
                .with(new AuthenticationHttpFilters(), customizer -> {})
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setHeader("Access-Control-Allow-Origin", "*");
                            response.sendError(
                                    HttpStatus.UNAUTHORIZED.value(),
                                    "Authorization failure. Session may have expired."
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setHeader("Access-Control-Allow-Origin", "*");
                            response.sendError(
                                    HttpStatus.FORBIDDEN.value(),
                                    "Access denied for the requested resource."
                            );
                        })
                )

                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .addFilterBefore(
                        authenticationJwtTokenFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/read/login")

                        .successHandler(oAuth2LoginSuccessHandler)

                        .userInfoEndpoint(user -> user
                                .userService(customOAuth2UserService)
                        )
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/read/login")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/**").permitAll()
//                        .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    public InternalResourceViewResolver defaultViewResolver() {
        return new InternalResourceViewResolver();
    }
}