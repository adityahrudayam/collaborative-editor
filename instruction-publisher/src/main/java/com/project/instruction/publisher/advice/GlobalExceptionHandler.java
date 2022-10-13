package com.project.instruction.publisher.advice;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.project.instruction.publisher.customexceptions.DocumentNotFoundException;
import com.project.instruction.publisher.customexceptions.InvalidRequestDataException;
import com.project.instruction.publisher.customexceptions.UnauthorizedAccessException;
import com.project.instruction.publisher.customexceptions.UserNotFoundException;

@ControllerAdvice
@Component
public class GlobalExceptionHandler {
	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Map<String, String>> userNotFoundHandler(UserNotFoundException err) {
		return new ResponseEntity<Map<String, String>>(Map.of("Error msg", err.getMessage()), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(DocumentNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Map<String, String>> documentNotFoundHandler(DocumentNotFoundException err) {
		return new ResponseEntity<>(Map.of("Error msg", err.getMessage()), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidRequestDataException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Map<String, String>> invalidDocumentDataHandler(InvalidRequestDataException err) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error msg", err.getMessage()));
	}

	@ExceptionHandler(UnauthorizedAccessException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<Map<String, String>> unauthorizedAccessDataHandler(UnauthorizedAccessException err) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("Error msg", err.getMessage()));
	}
}
