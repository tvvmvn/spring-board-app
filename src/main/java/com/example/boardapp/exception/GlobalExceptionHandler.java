package com.example.boardapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  // 1. 로거 생성 (이 클래스 이름으로 로그가 찍힙니다)
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // 1. 자바 표준 예외 (400 Bad Request) 전담
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleIllegalArgument(IllegalArgumentException e, Model model) {

    // 서버 콘솔에 에러 상세 정보 출력!
    logger.error("잘못된 요청 발생: {}", e.getMessage());
    
    model.addAttribute("status", 400);
    // "이미 사용 중인 아이디입니다" 등
    model.addAttribute("message", e.getMessage()); 
    
    return "error/common-error"; // common-error.html 파일로 매핑!
  }

  // 2. 나머지 알 수 없는 모든 예외 (500 Server Error) 전담
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleAllException(Exception e, Model model) {

    // 가장 중요한 부분! 500 에러는 전체 스택 트레이스를 다 찍어야 함
    logger.error("서버 내부 오류 발생: {}", e.getMessage());
    
    model.addAttribute("status", 500);
    model.addAttribute("message", "서버 내부에 알 수 없는 오류가 발생했습니다.");
    
    return "error/common-error"; // 동일한 마스터 에러 페이지로 매핑!
  }

  // 즉, 뜬금없는 URL로 접속하면 우리가 의도한 400(IllegalArgumentException)이나 500(Exception)이 아니라, 
  // 스프링 표준인 404 에러가 발생하는데, 현재 핸들러에는 404를 처리하는 예외가 없으니 
  // 스프링이 기본 에러 페이지(Whitelabel Error Page)를 띄우거나 엉뚱한 예외로 간주하여 500 에러를 뱉는 겁니다.

  // 이를 깔끔하게 잡아내어 우리가 만든 common-error.html로 보내려면 NoHandlerFoundException을 핸들러에 추가해 줘야 합니다.
}