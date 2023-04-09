package com.dong.demo.v1.util;

import java.security.NoSuchAlgorithmException;

public class KeyCompressor {

    // folderPublicKey 를 folderCP 로 만들거나, accountCP 를 accountCP 로 만든다.
    // key(String) -> SHA256(byte[]) -> Base58(String)
    public static String compress(String key) throws NoSuchAlgorithmException {
        byte[] hash = SHA256.encrypt(key);
        return Base58.encode(hash);
    }
}
