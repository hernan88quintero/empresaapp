package com.hernan.empresaapp.venezuelaexchange.exception;

import com.hernan.empresaapp.dto.ErrorResponseDTO;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.hernan.empresaapp.venezuelaexchange")
public class VenezuelaExchangeExceptionHandler {

    @ExceptionHandler(InvalidExchangeRequestException.class)
    ResponseEntity<ErrorResponseDTO> invalidRequest(InvalidExchangeRequestException ex) {
        return response(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ExchangeRateUnavailableException.class, VenezuelaExternalApiException.class})
    ResponseEntity<ErrorResponseDTO> unavailable(RuntimeException ex) {
        return response(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    private ResponseEntity<ErrorResponseDTO> response(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ErrorResponseDTO(message, status.value(), LocalDateTime.now()));
    }
}
