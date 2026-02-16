package com.minierp.mini_erp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1) Yetkisiz erişim: AccessDeniedException → 403 + mesaj
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse response = new ErrorResponse("Bu işlem için yetkiniz bulunmamaktadır!");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 2) Kayıt bulunamadı vb. senaryolar: RuntimeException → 404 + mesaj
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        ErrorResponse response = new ErrorResponse("Aradığınız kayıt sistemde bulunamadı.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // JSON cevabında "mesaj" alanını taşıyan basit DTO
    public static class ErrorResponse {
        private String mesaj;

        public ErrorResponse(String mesaj) {
            this.mesaj = mesaj;
        }

        public String getMesaj() {
            return mesaj;
        }

        public void setMesaj(String mesaj) {
            this.mesaj = mesaj;
        }
    }
}