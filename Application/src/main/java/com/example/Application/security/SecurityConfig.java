package com.example.Application.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/patients/**").authenticated()  // endpoint protection /patients
                        .anyRequest().permitAll() // other request are available without authorization
                )
                .httpBasic() // enable basic authorization
                .and()
                .csrf().disable(); // disable CSRF for easy of testing via Postman
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // create a user
        UserDetails user = User.withUsername("user")
                .password(passwordEncoder().encode("password")) // encrypt the password
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // use BCrypt for passwords encryption
    }
}
