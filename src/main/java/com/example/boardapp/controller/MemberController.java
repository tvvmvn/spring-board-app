package com.example.boardapp.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.boardapp.domain.Member;
import com.example.boardapp.dto.MemberJoinRequest;
import com.example.boardapp.repository.MemberRepository;

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
  public String join(@ModelAttribute("memberForm") MemberJoinRequest dto, Model model) {

    try {
      dto.validate();

      // 2️⃣ 중복 회원 검사처럼 DB 조회가 필요한 비즈니스 검증만 컨트롤러/서비스에서 수행
      if (memberRepository.findByUsername(dto.getUsername()).isPresent()) {
        throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
      }

      // 🔥 [변경 완료] Setter 체이닝을 없애고 생성자를 통해 완제품 회원 객체 생성!
      Member member = new Member(
          dto.getUsername(),
          passwordEncoder.encode(dto.getPassword()),
          "ROLE_USER"
      );
      memberRepository.save(member);

      return "redirect:/member/login";
      
    } catch (Exception e) {
      model.addAttribute("errorMessage", e.getMessage());
      model.addAttribute("memberForm", dto);
      return "member/join";
    }
  }

  // 💡 커스텀 로그인 페이지 연결 통로 (POST는 시큐리티가 가로챔)
  @GetMapping("/login")
  public String loginForm() {
    return "member/login";
  }
}