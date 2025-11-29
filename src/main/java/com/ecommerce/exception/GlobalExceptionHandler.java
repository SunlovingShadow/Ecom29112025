package com.ecommerce.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("success", false);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("error", "Resource Not Found");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("success", false);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
   
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("error", "Unauthorized");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("success", false);

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

        String message;
        String originalMessage = ex.getMessage();
        
        // Provide context-aware error messages
        if (originalMessage != null && originalMessage.contains("PaymentRequest")) {
            message = "Payment request cannot be null. Please provide orderId, amount, and method.";
        } else if (originalMessage != null && originalMessage.contains("OrderRequest")) {
            message = "Order request cannot be null. Please provide shippingAddress and other required fields.";
        } else if (originalMessage != null && originalMessage.contains("CartItemRequest")) {
            message = "Cart item request cannot be null. Please provide productId and quantity.";
        } else if (originalMessage != null && originalMessage.contains("Required request body is missing")) {
            message = "Request body cannot be null. Please provide a valid JSON body.";
        } else if (originalMessage != null && originalMessage.contains("JSON parse error")) {
            message = "Invalid JSON format. Please check your request body syntax.";
        } else {
            message = "Request body is missing or malformed. Please provide a valid JSON body.";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("error", "Bad Request");
        body.put("message", message);
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("success", false);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParameter(MissingServletRequestParameterException ex) {

        String paramName = ex.getParameterName();
        String message;
        
        switch (paramName) {
            case "cartTotal":
                message = "Cart total is required";
                break;
            case "userId":
                message = "User ID is required";
                break;
            case "code":
                message = "Coupon code is required";
                break;
            case "productId":
                message = "Product ID is required";
                break;
            case "quantity":
                message = "Quantity is required";
                break;
            default:
                // Convert camelCase to readable format: cartTotal -> Cart total
                String readable = paramName.replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase();
                readable = readable.substring(0, 1).toUpperCase() + readable.substring(1);
                message = readable + " is required";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("error", "Bad Request");
        body.put("message", message);
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("success", false);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("error", "Validation Error");
        body.put("details", errors);
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("success", false);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("success", false);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseErrors(DataAccessException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("error", "Database Error");
        body.put("message", "A database error occurred. Please try again later.");
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("success", false);

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("success", false);

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}