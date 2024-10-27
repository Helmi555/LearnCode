package SGBD_Project.example.LearnCode.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // Disable CSRF
                .authorizeRequests()
                .anyRequest().permitAll()  // Allow all requests
                .and()
                .httpBasic().disable()  // Disable HTTP Basic authentication
                .formLogin().disable(); // Disable form login

        return http.build();
    }

    public static String bCryptPasswordEncoder(String pass) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(pass);
    }

}
