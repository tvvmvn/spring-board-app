package com.example.boardapp.dto;

import jakarta.validation.constraints.NotBlank;

public class BoardWriteRequest {

  // null, 빈칸(""), 공백(" ") 싹 다 필터링
  @NotBlank(message = "제목은 필수 입력 항목입니다.") 
  private String title;
  
  @NotBlank(message = "본문 내용을 입력해 주세요.")
  private String content;

  public BoardWriteRequest() {}

  // Getter/Setter
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }
  
  public void setContent(String content) {
    this.content = content;
  }
}
