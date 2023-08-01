package com.items.etag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface ETagResponseEntity<T> {

	String getETag();

	T getBody();

	ResponseEntity<T> ok();

	ResponseEntity<T> httpStatus(HttpStatus httpStatus);

}
