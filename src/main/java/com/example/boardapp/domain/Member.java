package com.example.boardapp.domain;

public class Member {

  private Long id;
  private String username;
  private String password;
  private String role; // 스프링 시큐리티 연동용 권한 (ROLE_USER 등)

  // 💡 JPA 및 내부 바인딩용 기본 생성자
  public Member() {}

  // 1️⃣ [회원가입용 생성자] 아직 DB에 들어가기 전이라 ID가 없습니다.
  public Member(String username, String password, String role) {
    this.username = username;
    this.password = password;
    this.role = role;
  }

  // 2️⃣ [DB 조회 및 RowMapper용 생성자] 이미 번호표(ID)를 달고 나온 회원용입니다.
  public Member(Long id, String username, String password, String role) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.role = role;
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

  public String getRole() {
    return role;
  }

  // 아이디 정규식 검증 로직 (소문자 + 숫자 4~15자)
  public boolean isValidUsername() {
    return this.username != null && this.username.matches("^[a-z0-9]{4,15}$");
  }

  // 비밀번호 길이 검증
  public boolean isValidPassword() {
    return this.password != null && this.password.length() >= 6 && this.password.length() <= 20;
  }
}