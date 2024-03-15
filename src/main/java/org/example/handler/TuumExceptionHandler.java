package org.example.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.response.BaseResponse;
import org.example.exception.TuumBusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class TuumExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<BaseResponse> handleException(Exception e) {
        log.error("An error occured: {}", e.getMessage());

        BaseResponse baseResponse = createErrorResponse(e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(baseResponse);
    }

    @ExceptionHandler(value = {TuumBusinessException.class})
    public ResponseEntity<BaseResponse> handleBusinessException(TuumBusinessException e) {
        log.error("A business exception occured: {}", e.getMessage());

        BaseResponse baseResponse = createErrorResponse(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(baseResponse);
    }

    private BaseResponse createErrorResponse(String errorMessage) {
        return BaseResponse.builder().errorMessage(errorMessage).isSuccess(false).build();
    }
}
