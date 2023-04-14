package com.dong.demo.v1.util;

import com.dong.demo.v1.exception.CompressAlgorithmDeprecatedException;

import java.security.NoSuchAlgorithmException;

public class KeyCompressor {

    // folderPublicKey 를 folderCP 로 만들거나, accountCP 를 accountCP 로 만든다.
    // key(String, Base64인지는 상관이 없다. 그냥 모든 문자열을 압축하는 거니까) -> SHA256(byte[]) -> Base58(String)
    public static String compress(String key) {
        byte[] hash = new byte[0];
        try {
            hash = SHA256.encrypt(key);
        } catch (NoSuchAlgorithmException e) {
            throw new CompressAlgorithmDeprecatedException(e);
        }
        return Base58.encode(hash);
    }
}
