package com.example.boardapp.domain;

public class Member {

  private Long id;
  private String username;
  private String password;

  // 내부용 기본 생성자
  public Member() {}

  // [회원가입용 생성자] 아직 DB에 들어가기 전이라 ID가 없습니다.
  public Member(String username, String password) {
    this.username = username;
    this.password = password;
  }

  // Getter 
  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}