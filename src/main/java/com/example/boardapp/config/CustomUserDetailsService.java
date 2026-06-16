package com.example.boardapp.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.boardapp.domain.Member;
import com.example.boardapp.mapper.MemberMapper;

// 데이터베이스로부터 인증을 요청한 사용자의 정보를 가져오는 기능를 제공하는 서비스
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

    return User.builder()
        .username(member.getUsername())
        .password(member.getPassword())
        .build();
  }
}