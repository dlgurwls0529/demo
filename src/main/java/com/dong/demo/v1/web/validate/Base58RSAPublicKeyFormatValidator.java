package com.dong.demo.v1.web.validate;

import com.dong.demo.v1.util.Base58;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Base58RSAPublicKeyFormatValidator implements ConstraintValidator<ValidBase58RSAPublicKeyFormat, String> {

    private final RSAFormatValidator rsaFormatValidator;
    private final Base58FormatValidator base58FormatValidator;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        System.out.println("Base58RSAPublicKeyFormatValidator.isValid");
        boolean res = base58FormatValidator.validate(value) && !rsaFormatValidator.validatePublicKey(Base58.decode(value));
        System.out.println("res = " + res);
        return res;
    }
}
