package com.example.boardapp.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.boardapp.domain.Board;
import com.example.boardapp.domain.Member;
import com.example.boardapp.dto.BoardResponse;
import com.example.boardapp.dto.BoardWriteRequest;
import com.example.boardapp.repository.BoardRepository;
import com.example.boardapp.repository.MemberRepository;

@Controller
public class BoardController {

  private final BoardRepository boardRepository;
  private final MemberRepository memberRepository;

  public BoardController(BoardRepository boardRepository, MemberRepository memberRepository) {
    this.boardRepository = boardRepository;
    this.memberRepository = memberRepository;
  }

  @GetMapping("/board/list")
  public String list(Model model) {

    // 💡 기본 생성자로 빈 상자를 만들고, Setter로 값을 채워서 리스트로 수집(Collect)합니다.
    List<BoardResponse> dtos = boardRepository.findAll().stream()
        .map(board -> {
          BoardResponse dto = new BoardResponse(); // 1. 빈 DTO 객체 생성 (기본 생성자)

          // 2. Setter를 통해 원본 엔티티(board)의 값들을 이식
          dto.setId(board.getId());
          dto.setTitle(board.getTitle());
          dto.setContent(board.getContent());
          // 3. 연관된 회원 정보도 안전하게 추출하여 세팅
          dto.setMemberId(board.getMember().getId());
          dto.setMemberName(board.getMember().getUsername());

          return dto; // 4. 완성된 DTO 반환
        })
        .collect(Collectors.toList());

    model.addAttribute("boards", dtos);

    return "board/list";
  }

  @GetMapping("/board/write")
  public String writeForm(Model model) {
    
    model.addAttribute("boardForm", new BoardWriteRequest());
    
    return "board/write";
  }

  @PostMapping("/board/write")
  public String write(
    @ModelAttribute("boardForm") BoardWriteRequest dto, 
    Model model, 
    Principal principal) {

    try {
      dto.validate();

      Member loginMember = memberRepository.findByUsername(principal.getName())
          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));

      Board board = new Board(dto.getTitle(), dto.getContent(), loginMember);
      boardRepository.save(board);

      return "redirect:/board/list";

    } catch (Exception e) {
      model.addAttribute("errorMessage", e.getMessage());
      return "board/write";
    }
  }

  @GetMapping("/board/edit")
  public String editForm(@RequestParam Long id, Model model, Principal principal) {
    
    Board board = boardRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글: " + id));

    // 1️⃣ 기존 데이터를 DTO 바구니에 쏙쏙 담아줍니다.
    BoardWriteRequest dto = new BoardWriteRequest();
    dto.setTitle(board.getTitle());
    dto.setContent(board.getContent());

    // 2️⃣ 가방 이름을 명확하게 "boardForm"으로 지정해서 보냅니다.
    model.addAttribute("boardId", id); // 어떤 글을 수정하는지 ID도 같이 보냄
    model.addAttribute("boardForm", dto);

    return "board/edit";
  }

  @PostMapping("/board/edit")
  public String edit(
    @RequestParam("id") Long id, 
    @ModelAttribute("boardForm") BoardWriteRequest dto, 
    Model model, 
    Principal principal) {
    
    Board board = boardRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글"));
    
    Member loginMember = memberRepository.findByUsername(principal.getName())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

    // 백엔드 데이터 변조 대비 소유권 2차 보안 검증
    if (!board.getMember().getId().equals(loginMember.getId())) {
      return "redirect:/board/list";
    }

    try {
      dto.validate();

    } catch (Exception e) {
      model.addAttribute("errorMessage", e.getMessage());
      model.addAttribute("boardId", id);
      model.addAttribute("boardForm", dto);
      return "board/edit";
    }
    
    // 💡 옛날 방식: board.setTitle(...); board.setContent(...); (X)
    // 💡 변경 방식: 엔티티의 핵심 비즈니스 메서드 딱 하나만 깔끔하게 호출! (O)
    board.updatePost(request.getTitle(), request.getContent());

    boardRepository.update(board); // 영속화 (업데이트)

    return "redirect:/board/list";
  }

  @PostMapping("/board/delete")
  public String delete(@RequestParam Long id, Principal principal) {

    Board board = boardRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글: " + id));
    
    Member loginMember = memberRepository.findByUsername(principal.getName())
      .orElse(null);

    // 💡 확실히 본인이 작성한 글일 때만 레포지토리에 격추 명령 하달
    if (board.getMember().getId().equals(loginMember.getId())) {
      boardRepository.delete(id);
    }
    
    return "redirect:/board/list";
  }
}