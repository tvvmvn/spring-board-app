package com.example.boardapp.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.example.boardapp.domain.Post;

@Mapper
public interface PostMapper {

  // 1. 글 전체 조회
  List<Post> findAll();

  // 2. 글 상세 조회 (컨트롤러에서 단건 조회할 때 필요!)
  Optional<Post> findById(Long id);

  // 3. 글 작성
  void save(Post post);

  // 4. 글 수정 (여기가 누락됐었습니다!)
  void update(Post post);

  // 5. 글 삭제 (여기도 추가!)
  void delete(Long id);
}