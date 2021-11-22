package pl.adambaranowski.rsbackend.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.adambaranowski.rsbackend.model.UserRole;
import pl.adambaranowski.rsbackend.security.JpaUserDetailsService;
import pl.adambaranowski.rsbackend.security.filter.JwtFilter;
import pl.adambaranowski.rsbackend.security.jwt.JwtService;

import java.util.Arrays;

import static pl.adambaranowski.rsbackend.security.UrlsConstants.*;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer{

    private static final String ADMIN_AUTHORITY = UserRole.ADMIN.name();
    private static final String TEACHER_AUTHORITY = UserRole.TEACHER.name();
    private static final String STUDENT_AUTHORITY = UserRole.STUDENT.name();

    private final JwtService jwtService;
    private final JpaUserDetailsService jpaUserDetailsService;
    @Value("${rs.security.enabled}")
    private boolean isSecurityEnabled;

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/swagger-ui/**").permitAll();

        if (isSecurityEnabled) {
            enableJwtSecurity(http);
        }

        http.cors().configurationSource(corsConfigurationSource());
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.csrf().disable();
        http.headers().frameOptions().sameOrigin();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
        //configuration.setAllowCredentials(true);
        //the below three lines will add the relevant CORS response headers
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private void enableJwtSecurity(HttpSecurity httpSecurity) throws Exception {
        JwtFilter jwtFilter = new JwtFilter(jwtService, jpaUserDetailsService);
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        configureEquipmentEndpointAccess(httpSecurity);
        configureReservationsEndpointAccess(httpSecurity);
        configureRoomsEndpointAccess(httpSecurity);
        configureLoginEndpointAccess(httpSecurity);
        configureUsersEndpointAccess(httpSecurity);
    }

    private void configureEquipmentEndpointAccess(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .mvcMatchers(HttpMethod.GET, EQUIPMENT_ENDPOINT).hasAnyAuthority(ADMIN_AUTHORITY, TEACHER_AUTHORITY, STUDENT_AUTHORITY)
                .mvcMatchers(HttpMethod.GET, EQUIPMENT_ENDPOINT + "/*").hasAnyAuthority(ADMIN_AUTHORITY, TEACHER_AUTHORITY, STUDENT_AUTHORITY)
                .mvcMatchers(HttpMethod.POST, EQUIPMENT_ENDPOINT).hasAuthority(ADMIN_AUTHORITY)
                .mvcMatchers(HttpMethod.PUT, EQUIPMENT_ENDPOINT + "/*").hasAuthority(ADMIN_AUTHORITY)
                .mvcMatchers(HttpMethod.DELETE, EQUIPMENT_ENDPOINT + "*").hasAuthority(ADMIN_AUTHORITY);

    }

    private void configureLoginEndpointAccess(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .antMatchers(LOGIN_ENDPOINT).permitAll();
    }

    private void configureReservationsEndpointAccess(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .antMatchers(RESERVATION_ENDPOINT).hasAnyAuthority(ADMIN_AUTHORITY, TEACHER_AUTHORITY, STUDENT_AUTHORITY)
                .antMatchers(RESERVATION_ENDPOINT + "/*").hasAnyAuthority(ADMIN_AUTHORITY, TEACHER_AUTHORITY, STUDENT_AUTHORITY);

    }

    private void configureRoomsEndpointAccess(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .mvcMatchers(HttpMethod.GET, ROOM_ENDPOINT).hasAnyAuthority(STUDENT_AUTHORITY, TEACHER_AUTHORITY, ADMIN_AUTHORITY)
                .mvcMatchers(HttpMethod.PUT, ROOM_ENDPOINT).hasAuthority(ADMIN_AUTHORITY)
                .mvcMatchers(HttpMethod.POST, ROOM_ENDPOINT).hasAuthority(ADMIN_AUTHORITY)
                .mvcMatchers(HttpMethod.GET, ROOM_ENDPOINT + "/*").hasAnyAuthority(ADMIN_AUTHORITY, TEACHER_AUTHORITY, STUDENT_AUTHORITY)
                .mvcMatchers(HttpMethod.DELETE, ROOM_ENDPOINT + "/*").hasAuthority(ADMIN_AUTHORITY);
    }

    private void configureUsersEndpointAccess(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .mvcMatchers(HttpMethod.POST, USERS_ENDPOINT).permitAll()
                .mvcMatchers(HttpMethod.GET, USERS_ENDPOINT).hasAuthority(ADMIN_AUTHORITY)
                .mvcMatchers(HttpMethod.GET, USERS_ENDPOINT+"/*").authenticated()
                .mvcMatchers(HttpMethod.DELETE, USERS_ENDPOINT+"/*").hasAuthority(ADMIN_AUTHORITY)
                .mvcMatchers(HttpMethod.POST, USERS_ENDPOINT+"/*").hasAuthority(ADMIN_AUTHORITY)
                .mvcMatchers(HttpMethod.PUT, USERS_ENDPOINT+"/*").hasAuthority(ADMIN_AUTHORITY);
    }
}
