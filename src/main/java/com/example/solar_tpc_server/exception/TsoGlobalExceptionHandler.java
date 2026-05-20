package com.example.solar_tpc_server.exception;

import com.example.solar_tpc_server.response.TsoApiResponse;
import com.example.solar_tpc_server.util.TsoMessageUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

/**
 * Nơi tập trung bắt và xử lý mọi Exception văng ra từ Controllers
 */
@RestControllerAdvice
public class TsoGlobalExceptionHandler {

    /**
     * Bắt lỗi nghiệp vụ chủ động văng ra (TsoAppException)
     */
    @ExceptionHandler(value = TsoAppException.class)
    public ResponseEntity<TsoApiResponse<Object>> handlingAppException(TsoAppException exception) {
        TsoErrorCode errorCode = exception.getErrorCode();
        
        // Resolve error message from TsoMessageUtil
        String resolvedMessage = TsoMessageUtil.getMessage("error." + errorCode.name().toLowerCase());
        if (resolvedMessage.equals("error." + errorCode.name().toLowerCase())) {
            resolvedMessage = errorCode.getMessage();
        }
        
        // Trả về TsoApiResponse bọc kèm theo message lấy từ enum TsoErrorCode hoặc từ exception
        String message = exception.getMessage() != null && !exception.getMessage().equals(errorCode.getMessage()) 
                            ? exception.getMessage() 
                            : resolvedMessage;
                            
        TsoApiResponse<Object> apiResponse = TsoApiResponse.error(errorCode.getCode(), message);

        return ResponseEntity
                .status(errorCode.getCode())
                .body(apiResponse);
    }

    /**
     * Bắt lỗi validate (MethodArgumentNotValidException)
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<TsoApiResponse<Object>> handlingValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        TsoApiResponse<Object> apiResponse = TsoApiResponse.<Object>builder()
                .statusCode(400)
                .message(TsoMessageUtil.getMessage("error.invalid_input"))
                .data(errors)
                .build();

        return ResponseEntity
                .status(400)
                .body(apiResponse);
    }

    /**
     * Bắt các lỗi chung (ví dụ lỗi RuntimeException không lường trước được)
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<TsoApiResponse<Object>> handlingRuntimeException(Exception exception) {
        TsoErrorCode errorCode = TsoErrorCode.INTERNAL_SERVER_ERROR;
        String resolvedMessage = TsoMessageUtil.getMessage("error.internal_server_error");
        if (resolvedMessage.equals("error.internal_server_error")) {
            resolvedMessage = errorCode.getMessage();
        }
        
        TsoApiResponse<Object> apiResponse = TsoApiResponse.error(
                errorCode.getCode(), 
                resolvedMessage + ": " + exception.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getCode())
                .body(apiResponse);
    }
}


