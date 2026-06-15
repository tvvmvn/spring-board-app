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
import com.example.boardapp.dto.PostResponse;
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

  // 3. 글 등록 처리 (@Valid로 들어오는 데이터 엄격하게 검증!)
  @PostMapping
  public String write(
      @Valid @ModelAttribute("postForm") PostWriteRequest dto,
      BindingResult bindingResult,
      Principal principal) {

    if (bindingResult.hasErrors()) {
      return "post/write"; // 에러가 있으면 튕구고 글쓰기 화면으로 빽 (에러 메시지 들고 감)
    }

    Member loginMember = memberMapper.findByUsername(principal.getName())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));

    Post post = new Post(dto.getTitle(), dto.getContent(), loginMember);
    postMapper.save(post);

    return "redirect:/posts";
  }

  @GetMapping
  public String list(Model model) {

    List<PostResponse> posts = postMapper.findAll().stream()
        .map((post) -> {
          // 1. 빈 DTO 객체 생성 (기본 생성자)
          PostResponse dto = new PostResponse(); 
          // 2. Setter를 통해 원본 엔티티(post)의 값들을 이식
          dto.setId(post.getId());
          dto.setTitle(post.getTitle());
          dto.setContent(post.getContent());
          // 3. 연관된 회원 정보도 안전하게 추출하여 세팅
          dto.setMemberId(post.getMember().getId());
          dto.setMemberName(post.getMember().getUsername());

          return dto; // 4. 완성된 DTO 반환
        })
        .collect(Collectors.toList());

    model.addAttribute("posts", posts);

    return "post/list";
  }

  // 2. 글 쓰기 화면 이동
  @GetMapping("/write")
  public String writeForm(Model model) {

    model.addAttribute("postForm", new PostWriteRequest());

    return "post/write";
  }

  @GetMapping("/edit")
  public String editForm(@RequestParam Long id, Model model) {

    Post post = postMapper.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글: " + id));

    // 기존 데이터를 DTO 바구니에 쏙쏙 담아줍니다.
    PostWriteRequest dto = new PostWriteRequest();
    dto.setTitle(post.getTitle());
    dto.setContent(post.getContent());

    // 가방 이름을 명확하게 "postForm"으로 지정해서 보냅니다.
    model.addAttribute("postId", id); // 어떤 글을 수정하는지 ID도 같이 보냄
    model.addAttribute("postForm", dto);

    return "post/edit";
  }

  // 5. 글 수정 처리 (등록과 동일하게 검증 적용)
  @PostMapping("/edit")
  public String modify(
      @RequestParam("id") Long id,
      @Valid @ModelAttribute("postForm") PostWriteRequest dto,
      BindingResult bindingResult,
      Model model,
      Principal principal) {

    // 검증 실패 시: id값을 다시 넘겨줘야 에러 화면에서도 수정 폼이 유지됨
    if (bindingResult.hasErrors()) {
      model.addAttribute("postId", id);

      return "post/edit";
    }

    Post post = postMapper.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글"));

    Member loginMember = memberMapper.findByUsername(principal.getName())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

    // 백엔드 데이터 변조 대비 소유권 2차 보안 검증
    if (post.getMember().getId().equals(loginMember.getId())) {
      post.updatePost(dto.getTitle(), dto.getContent());
      // 영속화 (업데이트)
      postMapper.update(post); 
    }

    return "redirect:/posts";
  }

  // 글 삭제 처리
  @PostMapping("/delete")
  public String remove(@RequestParam Long id, Principal principal) {

    Post post = postMapper.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글: " + id));

    Member loginMember = memberMapper.findByUsername(principal.getName())
        .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 존재하지 않습니다"));

    // 💡 확실히 본인이 작성한 글일 때만 레포지토리에 격추 명령 하달
    if (post.getMember().getId().equals(loginMember.getId())) {
      // db 삭제
      postMapper.delete(id);
    }

    return "redirect:/posts";
  }
}