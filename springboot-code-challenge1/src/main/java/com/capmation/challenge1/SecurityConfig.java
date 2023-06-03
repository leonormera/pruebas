package com.capmation.challenge1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	
        http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth.requestMatchers("/bankaccounts/**").permitAll())
               // .authorizeHttpRequests(auth -> auth.requestMatchers("/bankaccounts/**").hasRole("ACCOUNT-OWNER"))
                //.authorizeHttpRequests(auth -> auth.requestMatchers("/bankaccounts/**/deposit").hasAnyRole("ACCOUNT-OWNER","SOMETHING-ELSE"))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder users = User.builder();
        UserDetails user1 = users
                .username("user1")
                .password(passwordEncoder.encode("user1$$pwd"))
                .roles("ACCOUNT-OWNER") // new role
                .build();
        UserDetails user2 = users
                .username("user2")
                .password(passwordEncoder.encode("user2$$pwd"))
                .roles("ACCOUNT-OWNER")
                .build();
        UserDetails user3 = users
                .username("user3")
                .password(passwordEncoder.encode("user3$$pwd"))
                .roles("SOMETHING-ELSE") // new role
                .build();
        
        return new InMemoryUserDetailsManager(user1, user2, user3);
    }
}