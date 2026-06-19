package com.example.boardapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

// 컨트롤러에서 던진 예외를 처리하는 곳. 상황팀!
@ControllerAdvice
public class GlobalExceptionHandler {

  // 기록기(logger): 서버에서 일지(log)를 작성하는 사람
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // IllegalArgumentException 예외를 처리할 핸들러로 지정합니다.
  @ExceptionHandler(IllegalArgumentException.class)
  // 이 예외가 발생하면 응답 코드를 400(Bad Request)으로 설정합니다.
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleIllegalArgument(IllegalArgumentException e, Model model) {

    // 에러 사항을 콘솔에 출력합니다. (개발자 확인용)
    logger.error("잘못된 요청 발생: {}", e);
    
    // 뷰에게 에러 정보를 전달합니다.
    model.addAttribute("status", 400);
    model.addAttribute("message", e.getMessage()); 
    
    // 에러 페이지를 전송합니다.
    return "error"; 
  }

  // 기타 다른 예외들(Exception)을 처리하는 핸들러로 지정합니다.
  @ExceptionHandler(Exception.class)
  // 이 예외가 발생하면 응답 코드를 500(내부 서버 오류)로 설정합니다.
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleAllException(Exception e, Model model) {

    // 에러 사항을 콘솔에 출력합니다. (개발자 확인용)
    logger.error("서버 내부 오류 발생: {}", e);
    
    // 뷰에게 에러 정보를 전달하고
    model.addAttribute("status", 500);
    model.addAttribute("message", "서버에 문제가 발생했습니다.");
    
    // 에러 페이지를 전송합니다.
    return "error"; 
  }
}