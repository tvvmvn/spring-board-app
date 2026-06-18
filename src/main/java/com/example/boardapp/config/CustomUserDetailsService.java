package com.example.boardapp.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.boardapp.domain.Member;
import com.example.boardapp.mapper.MemberMapper;

// 데이터베이스로부터 인증을 요청한 사용자의 정보를 가져오는 서비스
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberMapper memberMapper;

  public CustomUserDetailsService(MemberMapper memberMapper) {
    this.memberMapper = memberMapper;
  }

  // UserDetails: 시큐리티가 인증에 사용하는 객체 규격
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    Member member = memberMapper.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다: " + username));

        // member로부터 UserDetails 객체를 생성합니다.
        return User.builder()
        .username(member.getUsername())
        .password(member.getPassword())
        .build();
  }
}