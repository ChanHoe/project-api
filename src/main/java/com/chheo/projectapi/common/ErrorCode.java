package com.chheo.projectapi.common;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
	NOT_FOUND("1000", HttpStatus.NOT_FOUND, "지자체 정보를 찾을 수 없습니다."),
	INVALID_PARAM("1001", HttpStatus.BAD_REQUEST, "파라메터 오류입니다."),
	UNKNOWN_ERROR("1002", HttpStatus.INTERNAL_SERVER_ERROR, "알수없는 오류 발생");

	private final String code;
	private final HttpStatus httpStatus;
	private final String message;

	public static ErrorCode getByCode(final String code) {
		for (final ErrorCode e : values()) {
			if (e.code.equals(code))
				return e;
		}
		return ErrorCode.UNKNOWN_ERROR;
	}
}
