package com.dong.demo.v1.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

class CipherUtilTest {

    // https://d2.naver.com/helloworld/227016

    /*
        JCA 에서 지원하는 모든 종류의 키는 상위 수준의 Java.security.Key 인터페이스로 추상화된다. Key 인터페이스를 통해서는 키의 모든 구성 요소에 직접적으로 접근하지는 못한다. Key 인터페이스는 getAlgorithm() 메서드와 getFormat() 메서드, getEncoded() 메서드 등 오직 세 가지 메서드를 제공하여 제한된 키 정보에 대한 접근만 허용하고 있다. 다음은 Key 인터페이스에서 제공하는 세 가지 키 정보다.

        알고리즘(Algorithm) 이름: 키 알고리즘은 보통 대칭키 암호 방식(AES 또는 DSA 등) 또는 비대칭키 연산 알고리즘(RSA)이다. getAlgorithm() 메서드로 키 알고리즘 이름을 얻는다.
        암호화된 형식(Encoded Form) 이름: 키가 외부에 노출되는 암호화된 형식이다. 키를 다른 당사자에게 전송하는 것처럼 JVM 외부에서 키가 필요할 때를 위해 표준 형식이 필요하다. 그 키는 표준 형식(예를 들어 X509 또는 PKCS8)을 따라 암호화된다. 이렇게 암호화된 키는 getEncoded() 메서드로 얻을 수 있다.
        암호화 포맷(Format) 이름: 키를 외부에 노출하기 위해 암호화한 포맷의 이름이다. getFormat() 메서드 포맷의 이름을 얻

     */


    @Test
    public void conversion_keyPair_Base58_to_String_test() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();

        PublicKey ex_publicKey = keyPair.getPublic();
        PrivateKey ex_privateKey = keyPair.getPrivate();

        // pkcs8 형식으로 인코딩된 키를 가져온다.
        byte[] publicKey_encoded = ex_publicKey.getEncoded();
        byte[] privateKey_encoded = ex_privateKey.getEncoded();

        // 그거를 다시 base58 스트링 변환
        String publicKey_base58_encoded = Base58.encode(publicKey_encoded);
        String privateKey_base58_encoded = Base58.encode(privateKey_encoded);

        // when
        // base58 스트링으로부터 키 객체를 얻는다.
        PublicKey ac_publicKey = CipherUtil.getPublicKeyFromBase58String(publicKey_base58_encoded);
        PrivateKey ac_privateKey = CipherUtil.getPrivateKeyFromBase58String(privateKey_base58_encoded);

        // then
        Assertions.assertNotSame(ex_publicKey, ac_publicKey);
        Assertions.assertNotSame(ex_privateKey, ac_privateKey);

        Assertions.assertEquals(ex_publicKey, ac_publicKey);
        Assertions.assertEquals(ex_privateKey, ac_privateKey);
    }


}