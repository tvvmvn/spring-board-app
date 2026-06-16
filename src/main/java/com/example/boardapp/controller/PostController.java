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

  @PostMapping
  public String write(
      @Valid @ModelAttribute("postForm") PostWriteRequest dto,
      BindingResult bindingResult,
      Principal principal) {

    if (bindingResult.hasErrors()) {
      return "post/write";
    }

    Member loginMember = memberMapper.findByUsername(principal.getName())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));

    Post post = new Post(dto.getTitle(), dto.getContent(), loginMember);
    postMapper.save(post);

    return "redirect:/posts";
  }

  @GetMapping
  public String list(Model model) {

    List<PostListResponse> posts = postMapper.findAll().stream()
        .map((post) -> {
          PostListResponse dto = new PostListResponse();
          dto.setId(post.getId());
          dto.setTitle(post.getTitle());
          dto.setContent(post.getContent());
          dto.setMemberId(post.getMember().getId());
          dto.setMemberName(post.getMember().getUsername());
          return dto;
        })
        .collect(Collectors.toList());

    model.addAttribute("posts", posts);

    return "post/list";
  }

  @GetMapping("/write")
  public String writeForm(Model model) {

    model.addAttribute("postForm", new PostWriteRequest());

    return "post/write";
  }

  @GetMapping("/edit")
  public String editForm(@RequestParam Long id, Model model) {

    Post post = postMapper.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글: " + id));

    PostWriteRequest dto = new PostWriteRequest();
    dto.setTitle(post.getTitle());
    dto.setContent(post.getContent());

    model.addAttribute("postId", id);
    model.addAttribute("postForm", dto);

    return "post/edit";
  }

  @PostMapping("/edit")
  public String modify(
      @RequestParam("id") Long id,
      @Valid @ModelAttribute("postForm") PostWriteRequest dto,
      BindingResult bindingResult,
      Model model,
      Principal principal) {

    if (bindingResult.hasErrors()) {
      model.addAttribute("postId", id);

      return "post/edit";
    }

    Post post = postMapper.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글"));

    Member loginMember = memberMapper.findByUsername(principal.getName())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

    if (post.getMember().getId().equals(loginMember.getId())) {
      post.updatePost(dto.getTitle(), dto.getContent());
      postMapper.update(post);
    }

    return "redirect:/posts";
  }

  @PostMapping("/delete")
  public String remove(@RequestParam Long id, Principal principal) {

    Post post = postMapper.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글: " + id));

    Member loginMember = memberMapper.findByUsername(principal.getName())
        .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 존재하지 않습니다"));

    if (post.getMember().getId().equals(loginMember.getId())) {
      postMapper.delete(id);
    }

    return "redirect:/posts";
  }
}