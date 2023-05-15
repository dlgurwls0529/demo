package com.dong.demo.v1.web.validate;

import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.util.CipherUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Base58FormatValidator.class)
class Base58FormatValidatorTest {

    @Autowired
    private Base58FormatValidator validator;

    @Test
    public void base58StringValidateSuccessTest() {
        // given
        byte[] given_decoded = new byte[]{1, 4, 2, 1, 21, 5, 21, 52, 5, 1, 12};
        String given_encoded = Base58.encode(given_decoded);
        Boolean isValid = null;
        
        // when
        isValid = validator.validate(given_encoded);
        
        // then
        Assertions.assertTrue(isValid);
    }

    @Test
    public void base58StringValidateFailTest() throws NoSuchAlgorithmException {
        // given
        Boolean isInValid = null;

        // when
        isInValid = (!validator.validate(null));
        isInValid = isInValid && (!validator.validate(""));
        isInValid = isInValid && (!validator.validate(
                Base64.getEncoder().encodeToString(CipherUtil.genRSAKeyPair().getPublic().getEncoded())
        ));
        isInValid = isInValid && (!validator.validate(
                Base64.getEncoder().encodeToString(CipherUtil.genRSAKeyPair().getPrivate().getEncoded())
        ));

        // then
        Assertions.assertTrue(isInValid);
    }

}