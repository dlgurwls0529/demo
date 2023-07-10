package com.dong.demo.v1.web.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class RSAPublicKeyEncodingFormatValidator implements ConstraintValidator<ValidRSAPublicKeyEncodingFormat, String> {

    private final RSAFormatValidator rsaFormatValidator;
    private final Base58FormatValidator base58FormatValidator;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // return base58FormatValidator.validate(value) && rsaFormatValidator.validatePublicKey(Base58.decode(value));
        if (value == null || value.length() == 0) {
            return false;
        }

        String pattern = "\\d+and\\d+";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(value);

        return matcher.matches();
    }
}
