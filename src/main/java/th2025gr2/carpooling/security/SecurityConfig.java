package th2025gr2.carpooling.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import th2025gr2.carpooling.service.AdminDetailsService;
import th2025gr2.carpooling.service.UserService;

@Configuration
public class SecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain adminFilterChain(HttpSecurity http, AdminDetailsService adminDetailsService) throws Exception {
        return http
                .securityMatcher("/admin/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login").permitAll()
                        .anyRequest().hasAnyRole("ADMIN", "SUPER_ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/admin/login?error=true")
                        .permitAll()
                )
                .authenticationProvider(adminAuthProvider(adminDetailsService))
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain userFilterChain(HttpSecurity http, UserService userService) throws Exception {
        return http
                .securityMatcher("/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**", "/login", "/error",
                                "/css/**", "/js/**", "/images/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/profile", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .authenticationProvider(userAuthProvider(userService))
                .build();
    }

    DaoAuthenticationProvider userAuthProvider(UserService userService) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    DaoAuthenticationProvider adminAuthProvider(AdminDetailsService adminDetailsService) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(adminDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
//        return cfg.getAuthenticationManager();
//    }
}