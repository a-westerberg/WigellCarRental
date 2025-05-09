package com.wigell.wigellcarrental.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

//SA
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    //SA
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .disable()
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // WIG-80-SJ
    @Bean
    public UserDetailsService userDetailsService() {

        // ADMIN
        UserDetails admin = User
                .withUsername("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build();

        // USERS
        UserDetails userOne = User
                .withUsername("19850101-1234")
                .password("{noop}1234")
                .roles("USER")
                .build();
        UserDetails userTwo = User
                .withUsername("19900215-5678")
                .password("{noop}5678")
                .roles("USER")
                .build();
        UserDetails userThree = User
                .withUsername("19751230-9101")
                .password("{noop}9101")
                .roles("USER")
                .build();
        UserDetails userFour = User
                .withUsername("19881122-3456")
                .password("{noop}3456")
                .roles("USER")
                .build();
        UserDetails userFive = User
                .withUsername("19950505-7890")
                .password("{noop}7890")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, userOne, userTwo, userThree, userFour, userFive);
    }

}
