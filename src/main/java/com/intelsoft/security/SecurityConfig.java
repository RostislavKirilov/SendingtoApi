//package com.intelsoft.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        // Disable CSRF (Cross-Site Request Forgery)
//        http.csrf().disable()
//                // Allow all requests without authentication
//                .authorizeHttpRequests((requests) -> requests
//                        .anyRequest().permitAll()
//                );
//        return http.build();
//    }
//}
