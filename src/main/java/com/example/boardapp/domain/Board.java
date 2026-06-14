package com.example.boardapp.domain;

public class Board {
  
  private Long id;
  private String title;
  private String content;
  private Member member; // 💡 작성자를 Member 객체 관계로 매핑합니다.

  public Board() {}

  // 💡 1. 생성할 때 쓰는 전용 생성자 (Setter 대신 생성 시점에 데이터를 딱 박아버림)
  public Board(String title, String content, Member member) {
    this.title = title;
    this.content = content;
    this.member = member;
  }

  // 2️⃣ 🌟 [추가 생성자] DB 조회 및 RowMapper용 (ID가 이미 존재하므로 ID까지 받음)
  public Board(Long id, String title, String content, Member member) {
    this.id = id;
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