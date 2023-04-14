package com.dong.demo.v1.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class Base58Test {

    @Test
    public void base58_encoding_decoding_test() {
        // given
        byte[] bytes = new byte[] { 1, 2, 3 };

        // when
        String encoded = Base58.encode(bytes);
        byte[] decoded = Base58.decode(encoded);

        // then
        Assertions.assertNotSame(bytes, decoded);
        Assertions.assertEquals(bytes.length, decoded.length);
        for(int i = 0; i < bytes.length; i++) {
            Assertions.assertEquals(bytes[i], decoded[i]);
        }
    }

}