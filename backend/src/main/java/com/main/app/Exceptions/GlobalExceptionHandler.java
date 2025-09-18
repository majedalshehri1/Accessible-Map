package com.main.app.Exceptions;


import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlaceNotFoundException.class)
    public ProblemDetail handlePlaceNotFound(PlaceNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Place Not Found");
        return pd;
    }

    @ExceptionHandler(DuplicatePlaceException.class)
    public ProblemDetail handleDuplicate(DuplicatePlaceException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Duplicate Place");
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
        pd.setTitle("Internal Server Error");
        return pd;
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatus(ResponseStatusException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
        pd.setTitle("Error");
        return pd;
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ProblemDetail handleDuplicateEmail(DuplicateEmailException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Duplicate Email");
        return pd;
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ProblemDetail handleDuplicateUsername(DuplicateUsernameException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Duplicate Username");
        return pd;
    }

    @ExceptionHandler(JwtValidationException.class)
    public ResponseEntity<?> handleJwtValidationException(JwtValidationException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<?> handleLocked(LockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "status", 403,
                        "error", "Forbidden",
                        "message", "المستخدم محظور"
                ));
    }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<Map<String, Object>> handleBadCreds() {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "البريد الإلكتروني أو كلمة المرور غير صحيحة."));
        }
    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequest(BadRequestException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Bad Request");
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("المدخلات غير صحيحة.");
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, msg);
        pd.setTitle("Bad Request");
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(jakarta.validation.ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .findFirst()
                .orElse("المدخلات غير صحيحة.");
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, msg);
        pd.setTitle("Bad Request");
        return pd;
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
           MissingServletRequestParameterException.class
    })
    public ProblemDetail handleBadRequestParams(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad Request");
        pd.setDetail("قيمة أو معامل طلب غير صحيح.");
        return pd;
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,String>> handleIntegrity(org.springframework.dao.DataIntegrityViolationException ex) {
        Map<String,String> body = Map.of("message", " user already submitted a survey");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }


    }



