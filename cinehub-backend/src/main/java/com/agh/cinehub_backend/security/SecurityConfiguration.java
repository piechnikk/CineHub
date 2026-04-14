package com.agh.cinehub_backend.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration{

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        http
                .cors((cors) -> cors
                        .configurationSource(apiConfigurationSource())
                )
                .csrf(csrf -> csrf.disable()) //todo wlaczyc csrf
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                    .loginProcessingUrl("/login")
                    .successForwardUrl("/login-success")
                    .failureForwardUrl("/login-failure")
                    .permitAll()
                )
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\": \"Logout successful\"}");
                        })
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true)
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, ex) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"message\": \"Please sign in\"}");
                        })
                );;
        return http.build();
    }


    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    UrlBasedCorsConfigurationSource apiConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
