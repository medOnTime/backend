package com.medOnTime.authService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Disable CSRF for development and APIs
                .authorizeHttpRequests()
                .requestMatchers("/login", "/auth/**").permitAll() // Allow unauthenticated access
                .anyRequest().authenticated() // All others need authentication
                .and()
                .httpBasic().disable() // Disable basic auth
                .formLogin().disable(); // Disable form login

        return http.build();
    }
}
