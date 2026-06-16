package com.example.boardapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignUpRequest {

  @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력하세요.")
  private String username;

  @Size(min = 6, message = "비밀번호는 최소 6글자 이상이어야 합니다.")
  private String password;
  
  @NotBlank(message = "비밀번호 재입력은 필수 항목입니다.")
  private String passwordConfirm;

  public SignUpRequest() {}

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