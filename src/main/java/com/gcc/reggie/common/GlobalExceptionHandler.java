package com.gcc.reggie.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;


/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /*
    异常处理方法
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException e){
        log.error(e.getMessage());

        if (e.getMessage().contains("Duplicate entry")){
            String name = e.getMessage().split(" ")[2];
            return R.error(name + " 已存在");
        }
        return R.error("未知错误");
    }


    @ExceptionHandler(CustomerException.class)
    public R<String> exceptionHandler(CustomerException e){
        log.error(e.getMessage());
        return R.error(e.getMessage());
    }
}
