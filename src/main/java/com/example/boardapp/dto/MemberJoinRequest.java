package com.example.boardapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MemberJoinRequest {

  @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해 주세요.")
  private String username;

  @Size(min = 6, message = "비밀번호는 최소 6글자 이상이어야 합니다.")
  private String password;
  
  // 강의 포인트: DB 테이블엔 없지만 화면 인증에 필요한 필드 분리
  // 🌟 [추가] 비밀번호 확인 필드 (형식 검증은 위 필드와 똑같이 맞춰줍니다)
  @NotBlank(message = "비밀번호 확인은 필수 항목입니다.")
  private String passwordConfirm;

  // 기본 생성자 (Spring의 데이터 바인딩 로직 강의용 필수 요소)
  public MemberJoinRequest() {}

  // Getter/Setter
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
}