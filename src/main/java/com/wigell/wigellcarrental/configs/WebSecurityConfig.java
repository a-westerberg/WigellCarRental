package com.wigell.wigellcarrental.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

//SA
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)//Kolla om det fungerar utan på PreAuthorize i controllers
public class WebSecurityConfig {

    //SA
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()//Ändra till admin senare
                        .anyRequest().permitAll()//Ändra senare till authorized
                )
                .csrf(csrf -> csrf
                        .disable()
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
        //Ändra till authenticated när UserDetailsService är inlagt, ta in PreAuthorize i controller klasserna också
    }
}
//.ignoringRequestMatchers("/h2-console/**")//på csrf, men den verkar inte behövas
