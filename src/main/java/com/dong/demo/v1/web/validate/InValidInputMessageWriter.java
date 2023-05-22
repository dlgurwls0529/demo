package com.dong.demo.v1.web.validate;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.util.List;

@Component
public class InValidInputMessageWriter {

    public String write(List<FieldError> errorList) {
        StringBuilder errorMessage = new StringBuilder();

        for (FieldError fieldError : errorList) {
            String fieldName = fieldError.getField();
            errorMessage.append(fieldName)
                    .append(" ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }

        return errorMessage.toString();
    }
}
