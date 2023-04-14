package com.dong.demo.v1.util;

import com.dong.demo.v1.exception.CompressAlgorithmDeprecatedException;
import com.dong.demo.v1.exception.VerifyInvalidInputException;

import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.Arrays;

public class RSAVerifier {

    public static boolean pseudo_verify(byte[] signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sign = Signature.getInstance("SHA256withRSA");

        //Calculating the signature
        //byte[] signature = signString.getBytes();
        byte[] bytes = "hi".getBytes();
        //Initializing the signature
        sign.initVerify(publicKey);
        sign.update(bytes);

        //Verifying the signature

        return sign.verify(signature);

    }

    public static boolean verify(byte[] signature, PublicKey publicKey) {
        // Signature sign = null;

        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            // Calculating the signature
            // byte[] signature = signString.getBytes();

            sign.initVerify(publicKey);
            sign.update(publicKey.getEncoded());

            // Verifying the signature
            return sign.verify(signature);

        } catch (SignatureException | InvalidKeyException e) {
            throw new VerifyInvalidInputException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new CompressAlgorithmDeprecatedException(e);
        }

    }
}
