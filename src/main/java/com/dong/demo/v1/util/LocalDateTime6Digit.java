package com.dong.demo.v1.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTime6Digit {
    public static LocalDateTime now() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

        return LocalDateTime.parse(localDateTime.format(formatter));
    }
}
