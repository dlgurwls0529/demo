package com.dong.demo.v1.util;

import com.dong.demo.v1.exception.VerifyInvalidInputException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CipherUtil {

    // 1024 비트 RSA 키 쌍을 생성
    public static KeyPair genRSAKeyPair() throws NoSuchAlgorithmException {
        // 랜덤 시드
        SecureRandom secureRandom = new SecureRandom();
        // 팩토리? RSA 생성기 룩업
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        // 시드랑 크기 넣고 초기화
        gen.initialize(2048, secureRandom);

        return gen.genKeyPair();
    }

    // Base64 인코딩된 문자열로 된 개인키를 개인키 타입 객체로 바꿔준다.
    public static PrivateKey getPrivateKeyFromBase64String(final String keyString)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        final String privateKeyString =
                keyString.replaceAll("\\n",  "").replaceAll("-{5}[ a-zA-Z]*-{5}", "");

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PKCS8EncodedKeySpec keySpecPKCS8 =
                new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString));

        return keyFactory.generatePrivate(keySpecPKCS8);
    }

    // Base64 인코딩된 문자열로 된 공개키로부터 공개키 객체를 얻는다.
    public static PublicKey getPublicKeyFromBase64String(final String keyString)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        final String publicKeyString =
                keyString.replaceAll("\\n",  "").replaceAll("-{5}[ a-zA-Z]*-{5}", "");

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec keySpecX509 =
                new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));

        return keyFactory.generatePublic(keySpecX509);
    }

    // Base58 인코딩된 문자열로 된 개인키를 개인키 타입 객체로 바꿔준다.
    public static PrivateKey getPrivateKeyFromBase58String(final String keyString)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        final String privateKeyString =
                keyString.replaceAll("\\n",  "").replaceAll("-{5}[ a-zA-Z]*-{5}", "");

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PKCS8EncodedKeySpec keySpecPKCS8 =
                new PKCS8EncodedKeySpec(Base58.decode(privateKeyString));

        return keyFactory.generatePrivate(keySpecPKCS8);
    }

    // Base58 인코딩된 문자열로 된 공개키로부터 공개키 객체를 얻는다.
    public static PublicKey getPublicKeyFromBase58String(final String keyString) {

        final String publicKeyString =
                keyString.replaceAll("\\n",  "").replaceAll("-{5}[ a-zA-Z]*-{5}", "");

        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpecX509 =
                    new X509EncodedKeySpec(Base58.decode(publicKeyString));

            return keyFactory.generatePublic(keySpecX509);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw  new VerifyInvalidInputException(e);
        }

    }
}
