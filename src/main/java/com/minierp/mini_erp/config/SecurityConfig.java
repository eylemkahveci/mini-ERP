package com.minierp.mini_erp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // Auth endpoint'i herkese açık
                    auth.requestMatchers("/api/auth/**").permitAll();
                    // Swagger UI ve API docs herkese açık (tüm path'leri kapsar)
                    auth.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", 
                                         "/swagger-resources/**", "/webjars/**").permitAll();
                    // Sonra rol bazlı kurallar
                    auth.requestMatchers("/api/categories/**").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.GET, "/api/products/**").hasAnyRole("ADMIN", "DEPO", "MUHASEBE");
                    auth.requestMatchers("/api/products/**").hasAnyRole("ADMIN", "DEPO");
                    auth.requestMatchers("/api/stock/**").hasAnyRole("ADMIN", "DEPO");
                    auth.requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "MUHASEBE");
                    auth.requestMatchers("/api/users/**").hasRole("ADMIN");
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.authenticationProvider(daoAuthenticationProvider());
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);  // DB'den kullanıcı yüklemek için
        provider.setPasswordEncoder(passwordEncoder());      // Şifre karşılaştırmaları için BCrypt
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt: güncel ve güvenli bir hash algoritması, otomatik salt kullanır
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new org.springframework.security.authentication.ProviderManager(daoAuthenticationProvider());
    }
}