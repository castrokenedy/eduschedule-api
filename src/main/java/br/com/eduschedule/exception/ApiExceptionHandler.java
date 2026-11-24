package br.com.eduschedule.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.util.LinkedHashMap;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ProblemDetail tratar(ApiException ex) {
        return ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validar(MethodArgumentNotValidException ex) {
        var erro = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Dados invalidos");
        var campos = new LinkedHashMap<String, String>();
        ex.getBindingResult().getFieldErrors().forEach(e -> campos.putIfAbsent(e.getField(), e.getDefaultMessage()));
        erro.setProperty("campos", campos);
        return erro;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail formato() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Corpo da requisicao invalido");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail integridade() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Registro duplicado ou vinculado a outro registro");
    }
}
