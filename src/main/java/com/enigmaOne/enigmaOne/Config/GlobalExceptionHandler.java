package com.enigmaOne.enigmaOne.Config;

// La clase GlobalExceptionHandler original fue movida/centralizada a
// `com.enigmaOne.enigmaOne.service.exception.GlobalExceptionHandler`.
// Se deja este archivo como placeholder comentado para evitar conflictos de bean.

/*
import com.enigmaOne.enigmaOne.service.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        String message = "El archivo excede el tamaño máximo permitido. Ajusta 'spring.servlet.multipart.max-file-size' en application.properties.";
        ApiResponse<Object> response = new ApiResponse<>(message, null);
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArg(IllegalArgumentException ex) {
        ApiResponse<Object> response = new ApiResponse<>(ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleOther(Exception ex) {
        ApiResponse<Object> response = new ApiResponse<>("Error interno: " + ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
*/
