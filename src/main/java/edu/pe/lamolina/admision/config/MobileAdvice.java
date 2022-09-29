package edu.pe.lamolina.admision.config;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pe.albatross.zelpers.miscelanea.PhobosException;

@ControllerAdvice(basePackages = {"edu.pe.lamolina.admision.movil"})
public class MobileAdvice extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler
    protected ResponseEntity<?> handleRestError(PhobosException ex, WebRequest request) {
        logger.debug("Error REST ::: ", ex);
        
        ObjectNode error = new ObjectNode(JsonNodeFactory.instance);
        error.put("error", ex.getMessage());
        error.put("status", HttpStatus.NOT_ACCEPTABLE.toString());
        
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
    }
}
