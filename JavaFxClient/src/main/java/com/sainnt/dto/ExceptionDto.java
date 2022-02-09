package com.sainnt.dto;

import lombok.Data;

@Data
public class ExceptionDto {
    private final String exceptionType;
    private final String details;
}
