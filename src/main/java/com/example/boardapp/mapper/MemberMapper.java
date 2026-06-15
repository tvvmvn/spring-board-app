package com.example.boardapp.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.example.boardapp.domain.Member;


@Mapper
public interface MemberMapper {
  
  void save(Member member);

  Optional<Member> findByUsername(String username);
}