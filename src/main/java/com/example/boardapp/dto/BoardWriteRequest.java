package com.example.boardapp.dto;

public class BoardWriteRequest {

  private String title;
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

  // 게시물 유효성 검사
  public void validate() {
    if (title != null && !title.trim().isEmpty()) {
      throw new IllegalArgumentException("제목을 입력하세요.");
    }
    if (content != null && !content.trim().isEmpty()) {
      throw new IllegalArgumentException("내용을 입력하세요.");
    }
  }
}
