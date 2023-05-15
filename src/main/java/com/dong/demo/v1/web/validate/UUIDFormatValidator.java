package com.dong.demo.v1.web.validate;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDFormatValidator {

    // cover when without hyphen
    public boolean validate(String uuidString) {
        try {
            // 하이픈이 없는 경우 하이픈을 추가하여 UUID 를 생성합니다.
            if (uuidString.length() == 32) {
                uuidString = uuidString.substring(0, 8) + "-" + uuidString.substring(8, 12) + "-" + uuidString.substring(12, 16) + "-" + uuidString.substring(16, 20) + "-" + uuidString.substring(20);
            }
            UUID uuid = UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
