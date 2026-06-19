package com.example.boardapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// 설정 파일로 지정합니다
@Configuration 
@EnableWebSecurity // 시큐리티 설정파일로 지정합니다.
public class SecurityConfig {

  // 설정파일 내부에서 사용할 수 있는 어노테이션
  // 이 메서드가 반환하는 객체를 빈으로 등록합니다.
  // 시큐리티 내부의 객체를 빈으로 등록해야 하는 경우 이 방식이 사용됩니다.
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        // 새 http요청에 대해서 접근 권한을 부여하는 절차
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/member/join", "/member/login").permitAll()
            .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage("/member/login")
            .loginProcessingUrl("/member/login")
            .defaultSuccessUrl("/posts", true)
            .permitAll())
        .logout(logout -> logout
            //  로그아웃을 처리할 주소 (form의 요청주소와 일치해야 함)
            .logoutUrl("/member/logout")
            // 로그아웃 후 이동할 주소
            .logoutSuccessUrl("/member/login")
            // 로그아웃하면 세션(인증 상태)을 무효화합니다 
            .invalidateHttpSession(true));

    return http.build();
  }
}