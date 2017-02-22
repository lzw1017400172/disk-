package com.kaishengit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by 刘忠伟 on 2017/2/22.
 * spring的自定义异常类，notfound
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFountException  extends RuntimeException{
}
