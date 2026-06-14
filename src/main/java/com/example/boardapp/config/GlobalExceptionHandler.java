package com.example.boardapp.config;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  // 1. 자바 표준 예외 (400 Bad Request) 전담
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleIllegalArgument(IllegalArgumentException e, Model model) {
    
    model.addAttribute("status", 400);
    // "이미 사용 중인 아이디입니다" 등
    model.addAttribute("message", e.getMessage()); 
    
    return "error/common-error"; // 🌟 common-error.html 파일로 매핑!
  }

  // 2. 나머지 알 수 없는 모든 예외 (500 Server Error) 전담
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleAllException(Exception e, Model model) {
    
    model.addAttribute("status", 500);
    model.addAttribute("message", "서버 내부에 알 수 없는 오류가 발생했습니다.");
    
    return "error/common-error"; // 🌟 동일한 마스터 에러 페이지로 매핑!
  }
}