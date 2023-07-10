package com.dong.demo.v1.web.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RSAPublicKeyEncodingFormatValidator.class)
public @interface ValidRSAPublicKeyEncodingFormat {

    String message() default "folderPublicKey format is invalid. It may be violate Base58 or RSAPublicKey Format or blank.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
