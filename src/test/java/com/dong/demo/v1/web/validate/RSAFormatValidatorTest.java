package com.dong.demo.v1.web.validate;

import com.dong.demo.v1.util.CipherUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

@SpringBootTest(value = "com.dong.demo.v1.web.validate.RSAFormatVer")
class RSAFormatValidatorTest {

    @Autowired
    private RSAFormatValidator validator;

    @RepeatedTest(3)
    public void RSAKeyPairValidSuccessTest() throws NoSuchAlgorithmException {
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        Boolean isPublicKeyValid;
        Boolean isPrivateKeyValid;

        // when
        isPublicKeyValid = validator.validatePublicKey(keyPair.getPublic().getEncoded());
        isPrivateKeyValid = validator.validatePrivateKey(keyPair.getPrivate().getEncoded());

        // then
        Assertions.assertNotNull(isPublicKeyValid);
        Assertions.assertNotNull(isPrivateKeyValid);
        Assertions.assertTrue(isPublicKeyValid && isPrivateKeyValid);
    }

    @Test
    public void RSAKeyPairValidFailTest() throws NoSuchAlgorithmException {
        Boolean isPublicKeyValid;
        Boolean isPrivateKeyValid;

        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();

        // when
        isPublicKeyValid = validator.validatePublicKey(keyPair.getPrivate().getEncoded());
        isPrivateKeyValid = validator.validatePrivateKey(keyPair.getPublic().getEncoded());

        // then
        Assertions.assertNotNull(isPublicKeyValid);
        Assertions.assertNotNull(isPrivateKeyValid);
        Assertions.assertFalse(isPublicKeyValid || isPrivateKeyValid);

        // given
        isPublicKeyValid = null;
        isPrivateKeyValid = null;

        // when
        isPublicKeyValid = validator.validatePublicKey(new byte[]{});
        isPrivateKeyValid = validator.validatePrivateKey(new byte[]{});

        // then
        Assertions.assertNotNull(isPublicKeyValid);
        Assertions.assertNotNull(isPrivateKeyValid);
        Assertions.assertFalse(isPublicKeyValid || isPrivateKeyValid);

        // given
        isPublicKeyValid = null;
        isPrivateKeyValid = null;

        // when
        isPublicKeyValid = validator.validatePublicKey(null);
        isPrivateKeyValid = validator.validatePrivateKey(null);

        // then
        Assertions.assertNotNull(isPublicKeyValid);
        Assertions.assertNotNull(isPrivateKeyValid);
        Assertions.assertFalse(isPublicKeyValid || isPrivateKeyValid);
    }
}