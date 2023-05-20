package com.dong.demo.v1.web.validate;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;


@Component
public class RSAFormatValidator {

    public boolean validatePublicKey(byte[] publicKeyBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            byte[] encoded = publicKey.getEncoded();
            return Arrays.equals(publicKeyBytes, encoded);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NullPointerException e) {
            return false;
        }
    }

    public boolean validatePrivateKey(byte[] privateKeyBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            byte[] encoded = privateKey.getEncoded();
            return Arrays.equals(privateKeyBytes, encoded);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NullPointerException e) {
            return false;
        }
    }
}
