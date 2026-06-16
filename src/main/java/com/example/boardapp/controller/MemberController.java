package com.example.boardapp.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.boardapp.domain.Member;
import com.example.boardapp.dto.SignUpRequest;
import com.example.boardapp.mapper.MemberMapper;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/member")
public class MemberController {

  private final MemberMapper memberMapper;
  private final PasswordEncoder passwordEncoder;

  public MemberController(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
    this.memberMapper = memberMapper;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping("/join")
  public String joinForm(Model model) {

    model.addAttribute("memberForm", new SignUpRequest());

    return "member/join";
  }

  @PostMapping("/join")
  public String join(
      @Valid @ModelAttribute("memberForm") SignUpRequest dto,
      BindingResult bindingResult,
      Model model) {

    if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
      bindingResult.rejectValue("passwordConfirm", "passwordMismatch", "비밀번호가 일치하지 않습니다.");
    }

    if (memberMapper.findByUsername(dto.getUsername()).isPresent()) {
      bindingResult.rejectValue("username", "duplicatedUsername", "이미 존재하는 아이디입니다.");
    }

    if (bindingResult.hasErrors()) {
      return "member/join";
    }

    Member member = new Member(dto.getUsername(), passwordEncoder.encode(dto.getPassword()));
    memberMapper.save(member);

    return "redirect:/member/login";
  }

  @GetMapping("/login")
  public String loginForm() {
    return "member/login";
  }
}