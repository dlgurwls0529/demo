package com.dong.demo.v1.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

class VerifyTest {

    /*  verify 과정 설명
        Signature signature = Signature.getInstance("SHA256withRSA");
        // 그냥 객체 생성

        String msg = "~~";
        // msg 는 signature 의 정답지? 같은 것이다. 클라이언트의 sign 에 대해, 이게 서명할 때 msg 를 활용했다~~ 라고 전제한다.
        // 호출자로부터 전달받은 sign 이 msg 와 같은 내용으로부터 만들어졌다면, verify 결과는 참이 되고 아니면 거짓이 된다.

        byte[] sign = ~~;
        // 우리는 클라이언트로 sign 을 입력받는다.
        // sign 이 진짜 msg 와 같은 내용이 서명된 것인지는 모른다. 의사적으로 그냥 가정하는 것이다.
        // 머신러닝으로 치면 msg 는 target 이고 sign 은 input 같은 느낌이다.

		signature.initVerify(publicKey);
		// publicKey 전달

		signature.update(msg.getBytes());
		// sign 에 사용되었을 것으로 예상되는 message 전달

		boolean verify = signature.verify(sign);
		// 메시지, private key (두개 합친게 sign)가 전부 올바를 경우 참. private key 가 중요하다.

		본 프로젝트에서 사용될 때에는 아마 이런 식일 것이다.

		function parameter : publicKey, sign

		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initVerify(publicKey);
		signature.update(publicKey);
		boolean result = signature.verify(signature);
     */

    @Test
    public void pseudo_verify_success_test() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, SignatureException, InvalidKeyException {
        // given
        String message = "hi";
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();  //
        PrivateKey privateKey = keyPair.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes());

        byte[] sign = signature.sign();  //

        // when
        boolean result = RSAVerifier.pseudo_verify(sign, publicKey);

        // then
        Assertions.assertTrue(result);
    }

    @Test
    public void pseudo_verify_fail_test() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, SignatureException, InvalidKeyException {
        // given
        String message = "hi";
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();  //
        PrivateKey privateKey = keyPair.getPrivate();

        KeyPair keyPair_false = CipherUtil.genRSAKeyPair();
        PublicKey publicKey_false = keyPair_false.getPublic();  //

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes());

        byte[] sign = signature.sign();  //

        // when
        boolean result = RSAVerifier.pseudo_verify(sign, publicKey_false);

        // then
        Assertions.assertFalse(result);
    }

    @Test
    public void real_verify_success_test() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, SignatureException, InvalidKeyException {
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();  //
        PrivateKey privateKey = keyPair.getPrivate();

        // 클라이언트에서 주어지는 값은 Base58 string publicKey(sign 에서도 쓴다.) 와 byte[] sign 이다.
        String input_publicKey = Base58.encode(publicKey.getEncoded());  // ...

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(publicKey.getEncoded());

        byte[] input_sign = signature.sign();  // ...

        // when
        boolean result = RSAVerifier.verify(input_sign, CipherUtil.getPublicKeyFromBase58String(input_publicKey));

        // then
        Assertions.assertTrue(result);
    }

    @Test
    public void real_verify_fail_test() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, SignatureException, InvalidKeyException {
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();  //
        PrivateKey privateKey = keyPair.getPrivate();

        // 클라이언트에서 주어지는 값은 Base58 string publicKey(sign 에서도 쓴다.) 와 byte[] sign 이다.
        String input_publicKey = Base58.encode(publicKey.getEncoded());  // ...

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(CipherUtil.genRSAKeyPair().getPrivate()); // 가짜 프라이빗 키, 다시 만들었다.
        signature.update(publicKey.getEncoded());

        byte[] input_sign = signature.sign();  // ...

        // when
        boolean result = RSAVerifier.verify(input_sign, CipherUtil.getPublicKeyFromBase58String(input_publicKey));

        // then
        Assertions.assertFalse(result);
    }

}