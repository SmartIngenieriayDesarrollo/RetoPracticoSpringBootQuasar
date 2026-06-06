package com.smart.reto.shared;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiError> noEncontrado(RecursoNoEncontradoException ex,
                                                 HttpServletRequest req) {
        ApiError error = ApiError.de(HttpStatus.NOT_FOUND.value(), "Not Found",
                ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ApiError> reglaNegocio(ReglaNegocioException ex,
                                                 HttpServletRequest req) {
        ApiError error = ApiError.de(HttpStatus.CONFLICT.value(), "Conflict",
                ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacion(MethodArgumentNotValidException ex,
                                               HttpServletRequest req) {
        List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatearError)
                .toList();
        ApiError error = new ApiError(java.time.OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(), "Bad Request",
                "Error de validacion en la peticion", req.getRequestURI(), detalles);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> accesoDenegado(AccessDeniedException ex,
                                                   HttpServletRequest req) {
        ApiError error = ApiError.de(HttpStatus.FORBIDDEN.value(), "Forbidden",
                "No tiene permisos para esta operacion", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> errorGenerico(Exception ex, HttpServletRequest req) {
        ApiError error = ApiError.de(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error", "Ocurrio un error inesperado",
                req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String formatearError(FieldError fe) {
        return fe.getField() + ": " + fe.getDefaultMessage();
    }
}
