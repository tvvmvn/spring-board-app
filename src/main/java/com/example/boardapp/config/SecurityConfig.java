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
    return new BCryptPasswordEncoder(); // 비밀번호 해시 암호화
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    
    http
      .authorizeHttpRequests(auth -> auth
          .requestMatchers("/member/join", "/member/login").permitAll() // 누구나 접근 가능
          .anyRequest().authenticated() // 글쓰기, 수정, 삭제는 무조건 로그인 필수
      )
      // 커스텀 로그인 설정 연동
      .formLogin(form -> form
          .loginPage("/member/login")           // 화면을 보여줄 GET 경로
          .loginProcessingUrl("/member/login")   // 시큐리티가 POST 요청을 낚아챌 주소
          .defaultSuccessUrl("/posts", true) // 로그인 성공 시 목록으로 이동
          .permitAll()
      )
      .logout(logout -> logout
          .logoutUrl("/member/logout")
          .logoutSuccessUrl("/member/login")
          .invalidateHttpSession(true)
      );

    return http.build();
  }
}