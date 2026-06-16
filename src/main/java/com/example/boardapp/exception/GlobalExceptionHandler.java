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

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleIllegalArgument(IllegalArgumentException e, Model model) {

    logger.error("잘못된 요청 발생: {}", e.getMessage());
    
    model.addAttribute("status", 400);
    model.addAttribute("message", e.getMessage()); 
    
    return "error/common-error"; 
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleAllException(Exception e, Model model) {

    logger.error("서버 내부 오류 발생: {}", e.getMessage());
    
    model.addAttribute("status", 500);
    model.addAttribute("message", "서버에 문제가 발생했습니다.");
    
    return "error/common-error"; 
  }
}