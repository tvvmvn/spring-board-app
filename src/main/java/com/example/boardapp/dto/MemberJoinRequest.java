package com.example.boardapp.dto;

public class MemberJoinRequest {

  private String username;
  private String password;
  // 강의 포인트: DB 테이블엔 없지만 화면 인증에 필요한 필드 분리
  private String passwordConfirm;

  // 기본 생성자 (Spring의 데이터 바인딩 로직 강의용 필수 요소)
  public MemberJoinRequest() {}

  // Getter
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPasswordConfirm() {
    return passwordConfirm;
  }

  public void setPasswordConfirm(String passwordConfirm) {
    this.passwordConfirm = passwordConfirm;
  }

  // 💡 [핵심] 스스로를 검증하고, 규칙에 위배되면 즉시 예외를 터뜨린다!
  public void validate() {
    if (username == null || !username.matches("^[a-z0-9]{4,15}$")) {
      throw new IllegalArgumentException("아이디는 영문 소문자/숫자 조합 4~15자여야 합니다.");
    }
    if (password == null || password.length() < 6 || password.length() > 20) {
      throw new IllegalArgumentException("비밀번호는 6~20자 사이여야 합니다.");
    }
    if (!password.equals(passwordConfirm)) {
      throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
    }
  }
}