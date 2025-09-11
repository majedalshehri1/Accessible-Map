package com.main.app.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
public class InvalidCredntialsException extends RuntimeException {
    public InvalidCredntialsException() {
        super("البريد الإلكتروني أو كلمة المرور غير صحيحة.");
    }
}