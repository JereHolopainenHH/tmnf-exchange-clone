package fi.jereholopainen.tmnf_exchange_clone.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import fi.jereholopainen.tmnf_exchange_clone.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                // Allow unauthenticated access to these routes
                .requestMatchers("/h2-console/**").permitAll()  
                .requestMatchers("/**").permitAll()  
                // Restrict access to authenticated users for other routes
                .requestMatchers("/upload", "/profile").authenticated()  
                .anyRequest().authenticated())  // Protect all other requests
            .formLogin(formLogin -> formLogin
                .loginPage("/login")  // Custom login page
                .loginProcessingUrl("/login")  // URL for processing login
                .defaultSuccessUrl("/home", true)  // Redirect to /home after login
                .failureUrl("/login?error=true"))  // Redirect to login on failure
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .permitAll())  // Allow logout for everyone
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedPage("/home"))  // Redirect to /home on access denied
            .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable));  // Disable for H2 console if needed

        return http.build();
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}