package com.dong.demo.v1.web.validate;

import com.dong.demo.v1.util.Base58;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

// todo : 이거 Base58 클래스로 통합하고, UTIL 클래스 다 빈으로 만들기. 이거 만들고 테스트하기
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
