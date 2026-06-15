package com.example.boardapp.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DBInitializer implements CommandLineRunner {

  private final JdbcTemplate jdbcTemplate;
  private final PasswordEncoder passwordEncoder;

  public DBInitializer(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
    this.jdbcTemplate = jdbcTemplate;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) throws Exception {
    
    System.out.println("[DB 초기화] 테이블을 초기화합니다...");

    // 자식 테이블(post)을 먼저 삭제해야 외래키 제약조건 에러가 나지 않습니다.
    jdbcTemplate.execute("DROP TABLE IF EXISTS post");
    jdbcTemplate.execute("DROP TABLE IF EXISTS member");

    // 1. 회원 테이블 생성
    jdbcTemplate.execute(
        "CREATE TABLE member (" +
        "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
        "  username VARCHAR(50) NOT NULL UNIQUE," +
        "  password VARCHAR(100) NOT NULL" +
        ")"
    );

    // 2. 게시글 테이블 생성 (작성자의 고유 식별값인 member_id를 외래키로 저장)
    jdbcTemplate.execute(
        "CREATE TABLE post (" +
        "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
        "  title VARCHAR(200) NOT NULL," +
        "  content TEXT NOT NULL," +
        "  member_id BIGINT," +
        "  FOREIGN KEY (member_id) REFERENCES member(id)" +
        ")"
    );

    System.out.println("[DB 초기화 완료] 깨끗한 테이블 구조가 준비되었습니다.");

    // 💡 테이블이 매번 DROP 되므로 중복 체크 없이 정석대로 바로 INSERT!
    String rawPassword = "123456";
    String encodedPassword = passwordEncoder.encode(rawPassword); // 시큐리티 암호화 도구 활용

    String sql = String.format(
        "INSERT INTO member (username, password) VALUES ('johndoe', '%s')", encodedPassword);

    jdbcTemplate.execute(sql);

    System.out.println("샘플 유저 johndoe를 추가했습니다.");
  }
}