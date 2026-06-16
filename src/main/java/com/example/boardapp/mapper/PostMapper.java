package com.example.boardapp.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.example.boardapp.domain.Post;

@Mapper
public interface PostMapper {

  List<Post> findAll();

  Optional<Post> findById(Long id);

  void save(Post post);

  void update(Post post);

  void delete(Long id);
}