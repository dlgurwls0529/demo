package com.dong.demo.v1.util;

import java.util.UUID;

public class UUIDGenerator {

    public static String createUUID() {
        return UUID.randomUUID().toString();
    }

    public static String createUUIDWithoutHyphen() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
