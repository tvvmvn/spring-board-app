package com.example.boardapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        // 접근 권한을 부여하는 절차
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/member/join", "/member/login").permitAll()
            .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage("/member/login")
            .loginProcessingUrl("/member/login")
            .defaultSuccessUrl("/posts", true)
            .permitAll())
        .logout(logout -> logout
            .logoutUrl("/member/logout")
            .logoutSuccessUrl("/member/login")
            // 로그아웃하면 세션을 무효화합니다 
            .invalidateHttpSession(true));

    return http.build();
  }
}