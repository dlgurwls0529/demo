package com.dong.demo.example.web;

import com.dong.demo.example.web.HelloResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HelloResponseDtoTest {

    @Test
    public void lombok_test() {
        String name = "test";
        int amount = 1000;

        HelloResponseDto dto = new HelloResponseDto(name, amount);

        Assertions.assertEquals(name, dto.getName());
        Assertions.assertEquals(amount, dto.getAmount());
    }

}