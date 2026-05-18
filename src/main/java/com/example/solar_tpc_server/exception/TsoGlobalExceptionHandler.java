package com.example.solar_tpc_server.exception;

import com.example.solar_tpc_server.response.TsoApiResponse;
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
        
        // Trả về TsoApiResponse bọc kèm theo message lấy từ enum TsoErrorCode
        String message = exception.getMessage() != null && !exception.getMessage().equals(errorCode.getMessage()) 
                            ? exception.getMessage() 
                            : errorCode.getMessage();
                            
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
                .message("Dữ liệu đầu vào không hợp lệ")
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
        
        TsoApiResponse<Object> apiResponse = TsoApiResponse.error(
                errorCode.getCode(), 
                errorCode.getMessage() + ": " + exception.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getCode())
                .body(apiResponse);
    }
}

