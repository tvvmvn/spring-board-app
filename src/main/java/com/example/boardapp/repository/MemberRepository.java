package com.example.boardapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.boardapp.domain.Member;

@Repository
public class MemberRepository {

  // JDBC Template 주입
  private final JdbcTemplate jdbcTemplate;

  public MemberRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void save(Member member) {
    
    String sql = "INSERT INTO member (username, password, role) VALUES (?, ?, ?)";
    
    jdbcTemplate.update(sql, member.getUsername(), member.getPassword(), member.getRole());
  }

  public Optional<Member> findByUsername(String username) {
    
    String sql = "SELECT id, username, password, role FROM member WHERE username = ?";
    
    List<Member> result = jdbcTemplate.query(sql, memberRowMapper(), username);
    
    return result.stream().findAny();
  }

  private RowMapper<Member> memberRowMapper() {
    // result set
    return (rs, rowNum) -> {
      Member member = new Member(
        rs.getLong("id"),
        rs.getString("username"),
        rs.getString("password"),
        rs.getString("role")
      );

      return member;
    };
  }
}