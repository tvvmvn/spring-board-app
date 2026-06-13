package com.example.boardapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.boardapp.domain.Board;
import com.example.boardapp.domain.Member;

@Repository
public class BoardRepository {

  private final JdbcTemplate jdbcTemplate;

  public BoardRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  // 💡 저장할 때 Member의 PK 고유번호(id)를 외래키로 심어줍니다.
  public void save(Board board) {
    
    String sql = "INSERT INTO board (title, content, member_id) VALUES (?, ?, ?)";
    
    jdbcTemplate.update(sql, board.getTitle(), board.getContent(), board.getMember().getId());
  }

  // 💡 작성자의 문자열 username까지 한 번에 긁어오기 위해 JOIN 문을 씁니다.
  public List<Board> findAll() {

    String sql = "SELECT b.id, b.title, b.content, b.member_id, m.username " +
                 "FROM board b JOIN member m ON b.member_id = m.id ORDER BY b.id DESC";
    
    return jdbcTemplate.query(sql, boardRowMapper());
  }

  public Optional<Board> findById(Long id) {
    
    // board as b, member as m.
    String sql = "SELECT b.id, b.title, b.content, b.member_id, m.username " +
                 "FROM board b JOIN member m ON b.member_id = m.id WHERE b.id = ?";
    
    // 결과가 몇 개이든 일단 리스트에 저장하고
    List<Board> result = jdbcTemplate.query(sql, boardRowMapper(), id);
    
    // 스트림의 findAny() 메서드는 하나를 찾아 Optional 객체로 반환.
    return result.stream().findAny();
  }

  public void update(Board board) {
    
    String sql = "UPDATE board SET title = ?, content = ? WHERE id = ?";
    
    jdbcTemplate.update(sql, board.getTitle(), board.getContent(), board.getId());
  }

  public void delete(Long id) {
    
    String sql = "DELETE FROM board WHERE id = ?";
    
    jdbcTemplate.update(sql, id);
  }

  // 결과셋으로부터 Board 객체를 생성하는 역할
  private RowMapper<Board> boardRowMapper() {
    
    return (rs, rowNum) -> {//rs: result set

      // 💡 [변경 완료] 작성자 멤버 객체도 Setter를 빼고 조회용 생성자로 완벽하게 빌드!
      Member member = new Member(
          rs.getLong("member_id"),
          rs.getString("username"),
          null, // 게시판 목록 조회 시 비밀번호 정보는 안전하게 null 혹은 안 꺼내오기 처리
          null // 권한 정보도 필요 없다면 null 처리 (또는 DB 컬럼 추가 매핑)
      );

      // 2️⃣ 💡 [핵심] 생성자를 호출하면서 ResultSet에서 꺼낸 값들을 한 번에 밀어 넣습니다.
      // Setter 연쇄 호출을 완전히 걷어내고, 태어날 때부터 완벽한 완제품(Board)을 만듭니다.
      Board board = new Board(
          rs.getLong("id"),
          rs.getString("title"),
          rs.getString("content"),
          member);

      return board;
    };
  }
}