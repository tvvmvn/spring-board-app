package com.example.boardapp.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.boardapp.domain.Member;
import com.example.boardapp.domain.Post;
import com.example.boardapp.dto.PostDetailsResponse;
import com.example.boardapp.dto.PostListResponse;
import com.example.boardapp.dto.PostWriteRequest;
import com.example.boardapp.mapper.MemberMapper;
import com.example.boardapp.mapper.PostMapper;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/posts")
public class PostController {

  private final PostMapper postMapper;
  private final MemberMapper memberMapper;

  public PostController(PostMapper postMapper, MemberMapper memberMapper) {
    this.postMapper = postMapper;
    this.memberMapper = memberMapper;
  }

  @GetMapping("/write")
  public String writeForm(Model model) {

    // HTML의 postForm과 게시물-생성 dto를 매핑합니다.
    model.addAttribute("postForm", new PostWriteRequest());

    return "post/write";
  }

  @PostMapping
  public String write(
      // @Valid: 게시물 생성 dto를 대상으로 유효성 검사를 실행하라
      // @ModelAttribute: HTML의 postForm과 게시물-생성 dto를 매핑합니다.
      @Valid @ModelAttribute("postForm") PostWriteRequest dto,
      BindingResult bindingResult,
      // 인증이 완료된 사용자 정보(로그인한 사용자)를 저장하고 있는 객체
      Principal principal) {

    // 게시글 유효성 검사를 통과하지 못하면 에러와 함께 화면을 다시 렌더링시킵니다.
    if (bindingResult.hasErrors()) {
      return "post/write";
    }

    // 로그인한 멤버를 확인합니다.
    Member loginMember = memberMapper.findByUsername(principal.getName())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));

    // 작성자 멤버와 함께 게시물을 DB에 저장합니다.
    Post post = new Post(dto.getTitle(), dto.getContent(), loginMember);
    postMapper.save(post);

    // GET /posts/details 핸들러로 이동시킵니다. 
    return "redirect:/posts/details?id=" + post.getId();
  }

  @GetMapping
  public String list(Model model) {

    // 데이터베이스에서 가져온 게시물 리스트로부터 게시물dto 리스트를 생성합니다.
    List<PostListResponse> posts = postMapper.findAll().stream()
        .map((post) -> {
          PostListResponse dto = new PostListResponse();
          dto.setId(post.getId());
          dto.setTitle(post.getTitle());
          dto.setMemberName(post.getMember().getUsername());
          return dto;
        })
        .collect(Collectors.toList());

    // 모델: 뷰를 업데이트하는 역할
    // 모델이 posts 데이터를 템플릿 엔진에게 입력합니다. 
    // 뷰는 이 데이터를 바탕으로 화면을 완성합니다.
    model.addAttribute("posts", posts);

    return "post/list";
  }

  @GetMapping("/details")
  public String details(
    // 클라이언트가 전송한 게시물의 ID
    @RequestParam("id") Long id, 
    Model model) {

      // 게시물 검색
      Post post = postMapper.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물"));
      
      // 게시물로부터 게시물dto를 생성합니다
      PostDetailsResponse dto = new PostDetailsResponse();
      dto.setId(post.getId());
      dto.setTitle(post.getTitle());
      dto.setContent(post.getContent());
      dto.setMemberName(post.getMember().getUsername());

    // 뷰에게 dto를 주입합니다.
    model.addAttribute("post", dto);

    // 완성된 게시물 상세페이지 전송!
    return "post/details";
  }

  @PostMapping("/delete")
  public String remove(
    // 삭제를 요청한 게시물의 ID
    @RequestParam Long id, 
    // 인증이 완료된 사용자 정보
    Principal principal) {

    // 삭제할 게시물 검색
    Post post = postMapper.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글: " + id));

    // 로그인한 멤버를 확인합니다.
    Member loginMember = memberMapper.findByUsername(principal.getName())
        .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 존재하지 않습니다"));

    // 삭제를 요청한 사용자(loginMember)가 이 게시물의 작성자가 맞는지 확인합니다.
    if (post.getMember().getId().equals(loginMember.getId())) {
      postMapper.delete(id);
    }

    // GET /posts 핸들러로 이동시킵니다
    return "redirect:/posts";
  }
}