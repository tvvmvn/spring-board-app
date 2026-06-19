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

@Controller // 컨트롤러 빈으로 등록합니다
@RequestMapping("/member") // URL prefix. 
// 이 컨트롤러가 다루는 모든 URL의 주소 앞에 이 프리픽스를 붙여줍니다
public class MemberController {

  // 의존성 선언
  private final MemberMapper memberMapper;
  private final PasswordEncoder passwordEncoder;
  
  public MemberController(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
    // final로 선언한 멤버에게 값 할당을 안하면 컴파일 에러가 발생합니다.
    this.memberMapper = memberMapper;
    this.passwordEncoder = passwordEncoder;
  }

  // 회원가입 페이지 요청을 처리하는 핸들러
  @GetMapping("/join")
  // Model: MVC 패턴의 모델. 데이터를 활용해 뷰를 업데이트 하는 역할을 합니다.
  public String joinForm(Model model) {
    
    // 가입 페이지의 memberForm과 가입요청 dto(SignUpRequest)를 매핑합니다.
    model.addAttribute("memberForm", new SignUpRequest());

    // 회원가입 페이지를 클라이언트에게 전송합니다.
    return "member/join";
  }

  // 회원가입 처리 핸들러
  @PostMapping("/join")
  public String join(
      // dto: 클라이언트가 전송한 데이터가 이 dto에 저장되어있습니다.
      // @Valid: dto를 대상으로 사전에 정의한 유효성 검사를 실행하라
      // 가입 페이지의 memberForm과 가입요청 dto를 매핑합니다.
      @Valid @ModelAttribute("memberForm") SignUpRequest dto,
      // validation이 가입요청DTO(SignUpRequest)를 대상으로 유효성 검증을 마치고 그 결과를 담은 객체
      BindingResult bindingResult) {

    // 비밀번호와 비밀번호 확인(Password Confirm)이 일치하는지 검사합니다.
    if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
      // 일치하지 않으면 bindingResult에 직접 에러를 추가합니다.
      bindingResult.rejectValue("passwordConfirm", "passwordMismatch", "비밀번호가 일치하지 않습니다.");
    }

    // 사용자가 입력한 유저네임이 데이터베이스에 존재하는지 검사합니다.
    // isPresent: Optional의 메서드. 내부에 값이 존재하는지 확인합니다.
    if (memberMapper.findByUsername(dto.getUsername()).isPresent()) {
      // 사용자가 존재하면 '아이디 중복' 오류를 생성해 bindingResult에 직접 추가합니다.
      bindingResult.rejectValue("username", "duplicatedUsername", "이미 존재하는 아이디입니다.");
    }

    // validation의 검사 결과와 우리가 직접 수행한 검사 결과에 에러가 있다면.
    // 실패 이유와 함께 다시 클라이언트에게 가입 페이지를 전송합니다.
    if (bindingResult.hasErrors()) {
      return "member/join";
    }

    // 가입에 성공하면 멤버 객체를 생성합니다.
    Member member = new Member(dto.getUsername(), passwordEncoder.encode(dto.getPassword()));
    // 데이터베이스에 저장!
    memberMapper.save(member);

    // GET /member/login 핸들러로 이동시킵니다
    return "redirect:/member/login";
  }

  // 로그인 페이지 요청 핸들러
  @GetMapping("/login")
  public String loginForm() {
    // 로그인 페이지를 전송합니다.
    return "member/login";
  }
}