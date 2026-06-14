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
import com.example.boardapp.dto.MemberJoinRequest;
import com.example.boardapp.repository.MemberRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/member")
public class MemberController {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public MemberController(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping("/join")
  public String joinForm(Model model) {
    
    model.addAttribute("memberForm", new MemberJoinRequest());

    return "member/join";
  }

  @PostMapping("/join")
  public String join(
    @Valid @ModelAttribute("memberForm") MemberJoinRequest dto, 
    BindingResult bindingResult,
    Model model) {

      // 1️⃣ [추가] 두 비밀번호가 일치하는지 먼저 자바 코드로 검사!
    if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
      // bindingResult에 "passwordCheck" 필드 에러를 수동으로 심어버립니다.
      bindingResult.rejectValue("passwordConfirm", "passwordMismatch", "비밀번호가 일치하지 않습니다.");
    }

    // 2️⃣ 중복 회원 검사처럼 DB 조회가 필요한 비즈니스 검증만 컨트롤러/서비스에서 수행
    if (memberRepository.findByUsername(dto.getUsername()).isPresent()) {
      bindingResult.rejectValue("username", "usernameDuplication", "이미 존재하는 아이디입니다.");
    }

    // 1️⃣ [앞문 방어] 형식적인 검증 실패 시 (글자 수 미달, 이메일 누락 등)
    if (bindingResult.hasErrors()) {
      return "member/join"; // 회원가입 폼으로 튕기기 (입력값 유지)
    }

    // 🔥 [변경 완료] Setter 체이닝을 없애고 생성자를 통해 완제품 회원 객체 생성!
    Member member = new Member(
        dto.getUsername(),
        passwordEncoder.encode(dto.getPassword()),
        "ROLE_USER"
    );
    memberRepository.save(member);

    return "redirect:/member/login";
  }

  // 💡 커스텀 로그인 페이지 연결 통로 (POST는 시큐리티가 가로챔)
  @GetMapping("/login")
  public String loginForm() {
    return "member/login";
  }
}