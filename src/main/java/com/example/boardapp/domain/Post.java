package com.example.boardapp.domain;

public class Post {
  
  private Long id;
  private String title;
  private String content;
  private Member member; // 작성자를 Member 객체 관계로 매핑합니다.

  // 기본 생성자
  public Post() {}

  // 생성할 때 쓰는 전용 생성자 (Setter 대신 생성 시점에 데이터를 딱 박아버림)
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

  // 게시물 수정용
  public void updatePost(String title, String content) {
    this.title = title;
    this.content = content;
  }
}