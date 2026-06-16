package com.example.boardapp.domain;

public class Member {

  private Long id;
  private String username;
  private String password;

  public Member() {}

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