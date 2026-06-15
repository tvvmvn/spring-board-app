package com.example.boardapp.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.boardapp.domain.Member;
import com.example.boardapp.mapper.MemberMapper;

// 스프링 시큐리티가 로그인할 때 DB를 검사하게 만드는 핵심 연동 클래스입니다.
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberMapper memberMapper;

  public CustomUserDetailsService(MemberMapper memberMapper) {
    this.memberMapper = memberMapper;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    
    Member member = memberMapper.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다: " + username));

    // 스프링 시큐리티가 이해할 수 있는 UserDetails 객체로 변환해서 리턴합니다.
    return User.builder()
      .username(member.getUsername())
      .password(member.getPassword()) // DB에 암호화되어 저장된 비밀번호
      .build();
  }
}