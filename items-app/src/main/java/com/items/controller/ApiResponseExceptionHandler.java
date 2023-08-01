package com.items.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.items.exception.NotFoundException;
import com.items.exception.ValidationException;
import com.items.exception.general.ApplicationErrorException;
import com.items.exception.general.ErrorResponseMapper;
import com.items.model.ApiErrorResponseDTO;

import lombok.RequiredArgsConstructor;

/**
 * It represents a global exception handler.
 *
 */
@ControllerAdvice
@RequiredArgsConstructor
public class ApiResponseExceptionHandler {

	private final ErrorResponseMapper errorResponseMapper;

	@ExceptionHandler({ NotFoundException.class })
	public ResponseEntity<ApiErrorResponseDTO> handleNotFoundException(NotFoundException ex) {
		return processResponse(ex, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({ ValidationException.class })
	public ResponseEntity<ApiErrorResponseDTO> handleValidationException(ValidationException ex) {
		return processResponse(ex, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ ApplicationErrorException.class })
	public ResponseEntity<ApiErrorResponseDTO> handleApplicationErrorException(ApplicationErrorException ex) {
		return processResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<ApiErrorResponseDTO> processResponse(ApplicationErrorException ex, HttpStatus status) {
		ex.getApplicationError().setHttpStatus(status);
		final ApiErrorResponseDTO responseDTO = errorResponseMapper.errorToDTO(ex.getApplicationError());
		return ResponseEntity.status(responseDTO.getStatus()).headers(this.getDefaultHeaders()).body(responseDTO);
	}

	private HttpHeaders getDefaultHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;
	}
}
