package com.mxim.dmw.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class MyExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler({ DMWException.class })
	protected ResponseEntity<Object> handleInvalidRequest(Exception e, WebRequest request) {
		DMWException dmwException = (DMWException) e;
		List<FieldErrorResource> fieldErrorResources = new ArrayList<>();

		@SuppressWarnings("unchecked")
		List<FieldError> fieldErrors = dmwException.getErrors().getFieldErrors();
		for (FieldError fieldError : fieldErrors) {
			FieldErrorResource fieldErrorResource = new FieldErrorResource();
			fieldErrorResource.setResource(fieldError.getObjectName());
			fieldErrorResource.setField(fieldError.getField());
			fieldErrorResource.setCode(fieldError.getCode());
			fieldErrorResource.setMessage(fieldError.getDefaultMessage());
			fieldErrorResources.add(fieldErrorResource);
		}

		ErrorResource error = new ErrorResource("InvalidRequest", dmwException.getMessage());
		error.setFieldErrors(fieldErrorResources);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		return handleExceptionInternal(e, error, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}

}
