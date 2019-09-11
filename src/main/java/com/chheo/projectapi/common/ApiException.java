package com.chheo.projectapi.common;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiException extends RuntimeException{
	private ErrorCode code;
	private HttpStatus httpStatus;

	    public ApiException(ErrorCode errorCode) {
	        super(errorCode.getMessage());
	        this.code = errorCode;
	        this.httpStatus = errorCode.getHttpStatus();
	    }
}
