package antigravity.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(AntiGravityApplicationException.class)
    public ResponseEntity<?> applicationHandler(AntiGravityApplicationException e) {
        log.error("Error occur {}", e.toString());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(e.getErrorCode().name());
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> applicationHandler(RuntimeException e) {
        log.error("Error occur {}", e.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.toString());
    }
}