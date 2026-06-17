package com.example.boardapp.domain;

public class Post {
  
  private Long id;
  private String title;
  private String content;
  private Member member; 

  public Post() {}

  public Post(String title, String content, Member member) {
    this.title = title;
    this.content = content;
    this.member = member;
  }

  // Getter
  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public Member getMember() {
    return member;
  }
}