package com.dong.demo.v1.web.validate;

import com.dong.demo.v1.util.Base58;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Component
public class Base58FormatValidator {

    public boolean validate(String str) {
        if (str == null) {
            return false;
        }

        try {
            return Base58.encode(Base58.decode(str)).equals(str);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
