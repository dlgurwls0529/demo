package com.dong.demo.v1.web.validate;

import com.dong.demo.v1.util.UUIDGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = UUIDFormatValidator.class)
class UUIDFormatValidatorTest {

    @Autowired
    private UUIDFormatValidator validator;

    @Test
    public void testUUIDWithOutSuccessHyphen() {
        // given
        String uuid = UUIDGenerator.createUUID();

        // when, then
        Assertions.assertTrue(validator.validate(uuid));
    }

    @Test
    public void testUUIDWithOutFailHyphen() {
        // given
        // 16 진수라서 밑에 꺼는 통과한댄다.
        // String uuid = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        String uuid = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        Assertions.assertFalse(validator.validate(uuid));

        uuid = "&*(/&*(/&*(/&*(/&*(/&*(/&*(/&*(/";
        Assertions.assertFalse(validator.validate(uuid));
    }
}